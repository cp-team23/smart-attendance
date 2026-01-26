package com.capstoneproject.smartattendance.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.dto.Role;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;

import com.capstoneproject.smartattendance.entity.Academic;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Teacher;
import com.capstoneproject.smartattendance.entity.Student;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import com.capstoneproject.smartattendance.repository.AcademicRepo;
import com.capstoneproject.smartattendance.repository.AdminRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRepo;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.repository.TeacherRepo;
import com.capstoneproject.smartattendance.repository.UserRepo;

import com.capstoneproject.smartattendance.service.mail.AdminMailService;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepo userRepo;

    private final StudentRepo studentRepo;

    private final AdminRepo adminRepo;

    private final TeacherRepo teacherRepo;

    private final AcademicRepo academicRepo;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final AdminMailService adminMailService;

    private final OtpService otpService;

    private final AttendanceRepo attendanceRepo;

    @Value("${app.file.base-url}")
    private String fileBaseUrl;


    @Value("${file.upload-dir}")
    private String uploadDir;

    public List<AcademicDto> getAcademicDataService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<AcademicDto> response = admin.getAcademicDatas()
                .stream()
                .map(a ->{
                        AcademicDto res = modelMapper.map(a, AcademicDto.class);
                        res.setStudentCount(a.getStudents().size());
                        return res;
                    })
                .toList();

        return response;
    }
    @Transactional
    public void createAcademicDataService(AcademicDto academicDto, String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = modelMapper.map(academicDto, Academic.class);

        boolean exists = admin.getAcademicDatas().stream().anyMatch(a ->
            a.getYear().equalsIgnoreCase(academicDto.getYear()) &&
            a.getBranch().equalsIgnoreCase(academicDto.getBranch()) &&
            a.getSemester().equalsIgnoreCase(academicDto.getSemester()) &&
            a.getClassName().equalsIgnoreCase(academicDto.getClassName()) &&
            a.getBatch().equalsIgnoreCase(academicDto.getBatch())&&
            a.getAdmin().getUserId().equals(adminId)
        );

        if (exists) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }

        academic.setAdmin(admin);

        admin.getAcademicDatas().add(academic);
        adminRepo.save(admin);  

    }
    @Transactional
    public void updateAcademicDataService(AcademicDto academicDto, String adminId) {
        UUID academicId = academicDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        academic.setYear(academicDto.getYear());
        academic.setBranch(academicDto.getBranch());
        academic.setSemester(academicDto.getSemester());
        academic.setClassName(academicDto.getClassName());
        academic.setBatch(academicDto.getBatch());

        boolean exists = admin.getAcademicDatas().stream().anyMatch(a ->
            !a.getAcademicId().equals(academicId) &&
            a.getYear().equalsIgnoreCase(academicDto.getYear()) &&
            a.getBranch().equalsIgnoreCase(academicDto.getBranch()) &&
            a.getSemester().equalsIgnoreCase(academicDto.getSemester()) &&
            a.getClassName().equalsIgnoreCase(academicDto.getClassName()) &&
            a.getBatch().equalsIgnoreCase(academicDto.getBatch())
        );

        if (exists) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }
        academic.setAdmin(admin);
        admin.getAcademicDatas().add(academic);

        adminRepo.save(admin);
    }

    @Transactional
    public void deleteAcademicDataService(UUID academicId, String adminId) {
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (academic.getStudents().size() > 0) {
            throw new CustomeException(ErrorCode.CANT_DELETE_ACADEMIC);
        }
        academicRepo.deleteById(academicId);
    }

    public BasicDataDto getMyDetailsService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(admin, BasicDataDto.class);
        return basicDataDto;
    }

    public void updateAdminService(AdminDto adminDto, String adminId) {
        String collegeName = adminDto.getCollegeName();
        String name = adminDto.getName();
        String otp = adminDto.getOtp();
        String email = adminDto.getEmail();
        String password = adminDto.getPassword();
        
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        
        if(!passwordEncoder.matches(password,admin.getPassword())){
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }

        if (!admin.getEmail().equals(email)) {
            if (otp == null) {
                throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
            }
            otpService.verifyOtp(email, otp);
            admin.setEmail(email);
        }
        if (!collegeName.equals(admin.getCollegeName())) {
            admin.setCollegeName(collegeName);
            admin.setStudents(admin.getStudents().stream().peek(a -> a.setCollegeName(collegeName)).toList());
            admin.setTeachers(admin.getTeachers().stream().peek(a -> a.setCollegeName(collegeName)).toList());
        }
        admin.setName(name);

        adminRepo.save(admin);
    }

    public void addStudentService(StudentDto studentDto,String adminId) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (userRepo.findById(userId).isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        boolean flag = studentRepo.existsByCollegeNameAndEnrollmentNo(admin.getCollegeName(),studentDto.getEnrollmentNo());
        if(flag){
            throw new CustomeException(ErrorCode.ENROLLMENT_NOT_AVAILABLE);
        }
        Student student = modelMapper.map(studentDto, Student.class);

        student.setRole(Role.STUDENT);
        student.setAdmin(admin);
        student.setCollegeName(admin.getCollegeName());
        student.setAcademic(academic);
        student.setCurImage("defaultimage.jpg");
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(student, adminId, "created");
        studentRepo.save(student);
    }

    public void updateStudentService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        student.setName(studentDto.getName());
        student.setCollegeName(admin.getCollegeName());
        student.setEnrollmentNo(studentDto.getEnrollmentNo());
        student.setEmail(studentDto.getEmail());
        student.setAcademic(academic);
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        adminMailService.sendStudentDetailsMail(student, adminId, "updated");
        studentRepo.save(student);
    }

    public void deleteStudentService(String userId, String adminId) throws IOException {

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!student.getRole().equals(Role.STUDENT)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        if(student.getCurImage()!=null && student.getCurImage()!="defaultimage.jpg"){
            String curFile = student.getCurImage();
            Path curPath = uploadPath.resolve(curFile).normalize();
            Files.deleteIfExists(curPath);
        }
        studentRepo.deleteById(userId);
    }

    public List<StudentResponseDto> getAllImageChangeRequestService(String adminId){
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Student> students = studentRepo.findByNewImageIsNotNullAndAdmin_UserId(adminId);

        List<StudentResponseDto> response = students
                                .stream()
                                .map(s -> {
                                    StudentResponseDto res = modelMapper.map(s, StudentResponseDto.class);
                                    res.setYear(s.getAcademic().getYear());
                                    res.setBranch(s.getAcademic().getBranch());
                                    res.setSemester(s.getAcademic().getBranch());
                                    res.setClassName(s.getAcademic().getClassName());
                                    res.setBatch(s.getAcademic().getBatch());
                                    res.setCurImage(fileBaseUrl + res.getCurImage());
                                    res.setNewImage(fileBaseUrl + res.getNewImage());
                                    return res;
                                })
                                .toList(); 
        return response;   
    }

    public void approveImageChangeRequestService(String adminId,String userId) throws IOException{
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if(student.getNewImage()==null){
            throw new CustomeException(ErrorCode.NO_REQUEST_FOUND);
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        if(student.getCurImage()!=null && student.getCurImage()!="defaultimage.jpg"){
            String curFile = student.getCurImage();
            Path curPath = uploadPath.resolve(curFile).normalize();
            Files.deleteIfExists(curPath);
        }
        
        student.setCurImage(student.getNewImage());
        student.setNewImage(null);
        studentRepo.save(student);
        
    }

    public void rejectImageChangeRequestService(String adminId,String userId) throws IOException{
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if(student.getNewImage()==null){
            throw new CustomeException(ErrorCode.NO_REQUEST_FOUND);
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);

        String newFile = student.getNewImage();
        Path newPath = uploadPath.resolve(newFile).normalize();
        Files.deleteIfExists(newPath);

        student.setNewImage(null);
        studentRepo.save(student);        
    }

    public void addTeacherService(TeacherDto teacherDto, String adminId) {
        String userId = teacherDto.getUserId();
        String password = teacherDto.getPassword();

        if (userRepo.findById(userId).isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = modelMapper.map(teacherDto, Teacher.class);

        teacher.setRole(Role.TEACHER);
        teacher.setAdmin(admin);
        teacher.setCollegeName(admin.getCollegeName());
        teacher.setPassword(passwordEncoder.encode(password));

        adminMailService.sendTeacherDetailsMail(teacher, adminId, "created");
        teacherRepo.save(teacher);
    }

    public void updateTeacherService(TeacherDto teacherDto, String adminId) {
        String userId = teacherDto.getUserId();
        String password = teacherDto.getPassword();

        Teacher teacher = teacherRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!teacher.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        teacher.setName(teacherDto.getName());
        teacher.setEmail(teacherDto.getEmail());
        teacher.setAdmin(admin);
        teacher.setPassword(passwordEncoder.encode(password));

        adminMailService.sendTeacherDetailsMail(teacher, adminId, "updated");
        teacherRepo.save(teacher);
        
    }

    public void deleteTeacherService(String userId, String adminId) {

        Teacher teacher = teacherRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!teacher.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if (!teacher.getRole().equals(Role.TEACHER)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        teacherRepo.deleteById(userId);
    }

    public StudentResponseDto getStudentService(String enrollmentNo, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = admin.getStudents()
                .stream()
                .filter(a -> a.getEnrollmentNo().equals(enrollmentNo))
                .findFirst()
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Academic academic = student.getAcademic();
        StudentResponseDto studentResponseDto = modelMapper.map(student, StudentResponseDto.class);

        studentResponseDto.setYear(academic.getYear());
        studentResponseDto.setBranch(academic.getBranch());
        studentResponseDto.setSemester(academic.getSemester());
        studentResponseDto.setClassName(academic.getClassName());
        studentResponseDto.setBatch(academic.getBatch());
        studentResponseDto.setCurImage(fileBaseUrl + studentResponseDto.getCurImage());
        studentResponseDto.setNewImage(fileBaseUrl + studentResponseDto.getNewImage());
                        

        return studentResponseDto;
    }

    public BasicDataDto getTeacherService(String userId, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = admin.getTeachers()
                .stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);

        return basicDataDto;
    }

    public List<StudentResponseDto> getAllStudentService(UUID academicId,String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<StudentResponseDto> response = admin.getStudents()
                .stream()
                .filter(a->a.getAcademic().getAcademicId().equals(academicId))
                .map(a -> {
                        StudentResponseDto studentResponseDto = modelMapper.map(a, StudentResponseDto.class);
                        studentResponseDto.setYear(a.getAcademic().getYear());
                        studentResponseDto.setBranch(a.getAcademic().getBranch());
                        studentResponseDto.setSemester(a.getAcademic().getSemester());
                        studentResponseDto.setClassName(a.getAcademic().getClassName());
                        studentResponseDto.setBatch(a.getAcademic().getBatch());
                        studentResponseDto.setCurImage(fileBaseUrl + studentResponseDto.getCurImage());
                        studentResponseDto.setNewImage(fileBaseUrl + studentResponseDto.getNewImage());
                        return studentResponseDto;
                    })
                .toList();

        return response;
    }

    public List<BasicDataDto> getAllteacherService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<BasicDataDto> response = admin.getTeachers()
                .stream()
                .map(a -> modelMapper.map(a, BasicDataDto.class))
                .toList();

        return response;

    }

    @Transactional
    public void deleteAllAttendanceService(String adminId,String otp){
        if (otp == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Admin admin  = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        otpService.verifyOtp(admin.getEmail(),otp);

        for (Teacher teacher : admin.getTeachers()){
            attendanceRepo.deleteByTeacher(teacher);
        }         
    }
}
