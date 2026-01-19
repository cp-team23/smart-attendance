package com.capstoneproject.smartattendance.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.dto.StudentAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.entity.Academic;
import com.capstoneproject.smartattendance.entity.Attendance;
import com.capstoneproject.smartattendance.entity.AttendanceRecord;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.AttendanceAcademicRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRecordRepo;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.util.CryptoUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentService {
    
    private final StudentRepo studentRepo;

    private final AttendanceRecordRepo attendanceRecordRepo;

     private final AttendanceAcademicRepo attendanceAcademicRepo;

    private final ModelMapper modelMapper;

    private final StringRedisTemplate redisTemplate;

    private static final long QR_TTL_SECONDS = 120;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${app.file.base-url}")
    private String fileBaseUrl;


    public StudentResponseDto getMyDetailsService(String studentId) {
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        StudentResponseDto response = modelMapper.map(student, StudentResponseDto.class);
        Academic academic = student.getAcademic();
        response.setYear(academic.getYear());
        response.setBranch(academic.getBranch());
        response.setSemester(academic.getSemester());
        response.setClassName(response.getClassName());
        response.setBatch(response.getBatch());
        response.setCurImage(fileBaseUrl + response.getCurImage());
        response.setNewImage(fileBaseUrl + response.getNewImage());
        return response;
    }

    // change image req
    public void changeMyImageReqService(MultipartFile image, String studentId) throws IOException {
        if (image.isEmpty()) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (image.getSize() > 5 * 1024 * 1024) {
            throw new CustomeException(ErrorCode.FILE_SIZE_EXCEEDED);
        }

        String contentType = image.getContentType();
        if (contentType == null ||  !(contentType.equals("image/png")
                                    || contentType.equals("image/jpeg")
                                    || contentType.equals("image/jpg"))) {
            throw new CustomeException(ErrorCode.INVALID_FILE_TYPE);
        }

        String originalFilename = image.getOriginalFilename();
        if (originalFilename == null || !originalFilename.contains(".")) {
            throw new CustomeException(ErrorCode.INVALID_FILE_NAME);
        }
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!(extension.equals(".png") || extension.equals(".jpg") || extension.equals(".jpeg"))) {
            throw new CustomeException(ErrorCode.INVALID_FILE_TYPE);
        }
        
        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String prevFile = student.getNewImage();
        if (prevFile != null) {
            Path prevPath = uploadPath.resolve(prevFile).normalize();
            Files.deleteIfExists(prevPath);
        }
        // new image
        String fileName = UUID.randomUUID() + "_" + studentId + extension;
        Path filePath = uploadPath.resolve(fileName);
        Files.copy(image.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        student.setNewImage(fileName);
        studentRepo.save(student);

    }

    public void deleteMyImageReqService(String studentId) throws IOException {

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (student.getNewImage() == null) {
            throw new CustomeException(ErrorCode.NO_REQUEST_FOUND);
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String prevFile = student.getNewImage();
        Path prevPath = uploadPath.resolve(prevFile).normalize();
        Files.deleteIfExists(prevPath);

    }

    // scan qr code
    public void scanQRCodeService(String studentId, QRDto scanQRDto,String ipAddress) {

        Instant expireInstant = Instant.ofEpochMilli(scanQRDto.getExpireTime());

        if (expireInstant.isBefore(Instant.now())) {
            throw new CustomeException(ErrorCode.QR_EXPIRED);
        }
        UUID attendanceId = scanQRDto.getAttendanceId();
        String encryptedCode = scanQRDto.getEncryptedCode();
        Long expireTime = scanQRDto.getExpireTime();

        AttendanceRecord attendanceRecord = attendanceRecordRepo
                .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        Student student = studentRepo.findById(studentId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        boolean exist = attendanceAcademicRepo.existsByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId,student.getAcademic().getAcademicId());

        if(!exist){
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if(student.getCurImage()=="defaultimage.jpg"){
            throw new CustomeException(ErrorCode.IMAGE_NOT_FOUND);
        }

        String verificationCode = attendanceRecord.getAttendance().getVerificationCode();
        String attendanceKey = attendanceRecord.getAttendance().getAttendanceKey();
        String key = attendanceKey + expireTime;

        String decryptedCode = CryptoUtil.decrypt(encryptedCode,key);

        if (decryptedCode == null || !verificationCode.equals(decryptedCode)) {
            throw new CustomeException(ErrorCode.INVALID_QR_DATA);
        }

        String value = attendanceId + "|" + ipAddress;

        redisTemplate.opsForValue()
                .set("qr:" + studentId, value, QR_TTL_SECONDS, TimeUnit.SECONDS);

    }
    // match face
    

    // get all attendance
    public List<StudentAttendanceResponseDto> getMyAllAttendanceService(String studentId) {
        List<AttendanceRecord> attendanceRecords = attendanceRecordRepo.findByStudent_UserId(studentId);

        List<StudentAttendanceResponseDto> response = attendanceRecords
                .stream()
                .map(ar -> {
                    Attendance attendance = ar.getAttendance();
                    StudentAttendanceResponseDto sard = new StudentAttendanceResponseDto();
                    sard.setAttendanceId(attendance.getAttendanceId());
                    sard.setAttendanceDate(attendance.getAttendanceDate());
                    sard.setAttendanceTime(attendance.getAttendanceTime());
                    sard.setSubjectName(attendance.getSubjectName());
                    sard.setStatus(ar.getStatus());
                    return sard;
                })
                .toList();
        return response;
    }

}
