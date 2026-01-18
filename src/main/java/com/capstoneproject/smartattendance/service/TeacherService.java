package com.capstoneproject.smartattendance.service;

import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AtteandanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceDto;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceStatus;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.QRDto;
import com.capstoneproject.smartattendance.dto.StudentResponseDto;
import com.capstoneproject.smartattendance.entity.Academic;
import com.capstoneproject.smartattendance.entity.Attendance;
import com.capstoneproject.smartattendance.entity.AttendanceAcademic;
import com.capstoneproject.smartattendance.entity.AttendanceRecord;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.entity.Teacher;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.AcademicRepo;
import com.capstoneproject.smartattendance.repository.AttendanceAcademicRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRecordRepo;
import com.capstoneproject.smartattendance.repository.AttendanceRepo;
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.repository.TeacherRepo;
import com.capstoneproject.smartattendance.util.CryptoUtil;
import com.capstoneproject.smartattendance.util.RandomStringUtil;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final TeacherRepo teacherRepo;

    private final AcademicRepo academicRepo;

    private final StudentRepo studentRepo;

    private final AttendanceRepo attendanceRepo;

    private final AttendanceAcademicRepo attendanceAcademicRepo;

    private final AttendanceRecordRepo attendanceRecordRepo;

    private final ModelMapper modelMapper;

    private final AdminService adminService;

    public BasicDataDto getMyDetailsService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);
        return basicDataDto;
    }

    // create attendance
    public UUID createAttendanceService(AttendanceDto attendanceDto, String teacherId) {

        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        String verificationCode = RandomStringUtil.generate(12);
        String key = RandomStringUtil.generate(12);

        Attendance attendace = new Attendance();
        attendace.setTeacher(teacher);
        attendace.setAttendanceDate(attendanceDto.getAttendanceDate());
        attendace.setAttendanceTime(attendanceDto.getAttendaceTime());
        attendace.setVerificationCode(verificationCode);
        attendace.setAttendanceKey(key);
        attendace.setSubjectName(attendanceDto.getSubjectName());
        attendace.setRunning(false);

        attendace = attendanceRepo.save(attendace);

        List<AttendanceAcademic> attendanceAcademics = new ArrayList<>();
        List<AttendanceRecord> attendanceRecords = new ArrayList<>();

        for (UUID academicId : attendanceDto.getAcademicIds()) {

            Academic academic = academicRepo.findById(academicId)
                    .orElseThrow(() ->new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

            AttendanceAcademic aa = new AttendanceAcademic();
            aa.setAttendance(attendace);
            aa.setAcademic(academic);
            attendanceAcademics.add(aa);

            List<Student> students = studentRepo.findByAcademic_AcademicId(academicId);

            for (Student student : students) {
                AttendanceRecord record = new AttendanceRecord();
                record.setAttendance(attendace);
                record.setStudent(student);
                record.setStatus(AttendanceStatus.ABSENT); // default
                attendanceRecords.add(record);
            }
        }

        attendanceAcademicRepo.saveAll(attendanceAcademics);
        attendanceRecordRepo.saveAll(attendanceRecords);
        return attendace.getAttendanceId();
    }

    // start attendance
    public void startAttendanceService(UUID attendanceId,String teacherId) {
        if(attendanceId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setRunning(true);
        attendanceRepo.save(attendance);
    }

    // refresh attendance
    public QRDto refreshQRCodeService(UUID attendanceId,String teacherId,int refreshTime) {
        if(attendanceId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));
        
        long expireTime = Instant.now().plusSeconds(refreshTime).toEpochMilli();
        String key = attendance.getAttendanceKey() + expireTime;
        String encryptedCode = CryptoUtil.encrypt(attendance.getVerificationCode(),key);

        QRDto response = new QRDto();
        response.setAttendanceId(attendanceId);
        response.setExpireTime(expireTime);
        response.setEncryptedCode(encryptedCode);
        return response;
    }

    // stop attendance
    public void stopAttendanceService(UUID attendanceId,String teacherId) {
        if(attendanceId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setRunning(false);
        attendanceRepo.save(attendance);
    }
    // add new academic
    public void addNewAcademicInAttendanceService(UUID attendanceId,UUID academicId,String teacherId){
        if(attendanceId==null || academicId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                            .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                    .orElseThrow(() ->new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(attendance.getTeacher().getAdmin().getUserId())) {
            throw new CustomeException(ErrorCode.WRONG_ACADEMIC);
        }

        boolean alreadyAdded =attendanceAcademicRepo.existsByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId,academicId);
        if (alreadyAdded) {
            throw new CustomeException(ErrorCode.ACADEMIC_ALREADY_PRESENT);
        }

        AttendanceAcademic aa = new AttendanceAcademic();
        aa.setAttendance(attendance);
        aa.setAcademic(academic);
        attendanceAcademicRepo.save(aa);

        List<Student> students = studentRepo.findByAcademic_AcademicId(academicId);

        for (Student student : students) {
            boolean recordExists = attendanceRecordRepo.existsByAttendance_AttendanceIdAndStudent_UserId(attendanceId,student.getUserId());

            if (!recordExists) {
                AttendanceRecord record = new AttendanceRecord();
                record.setAttendance(attendance);
                record.setStudent(student);
                record.setStatus(AttendanceStatus.ABSENT);
                attendanceRecordRepo.save(record);
            }
        }
    }
    // remove academic
    public void removeAcademicInAttendanceService(UUID attendanceId,UUID academicId,String teacherId){
        
        if(attendanceId==null || academicId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        
        teacherRepo.findById(teacherId)
                            .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                    .orElseThrow(() ->new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(attendance.getTeacher().getAdmin().getUserId())) {
            throw new CustomeException(ErrorCode.WRONG_ACADEMIC);
        }

        boolean alreadyAdded =attendanceAcademicRepo.existsByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId,academicId);
        if (!alreadyAdded) {
            throw new CustomeException(ErrorCode.ACADEMIC_NOT_PRESENT);
        }
        attendanceAcademicRepo.deleteByAttendance_AttendanceIdAndAcademic_AcademicId(attendanceId,academicId);
        attendanceRecordRepo.deleteByAttendance_AttendanceIdAndStudent_Academic_AcademicId(attendanceId,academicId);

    }
    // delete attendance
    public void deleteAttendanceService(UUID attendanceId,String teacherId) {
        
        if(attendanceId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRepo.deleteById(attendanceId);
        
    }
    // get all attendance
    public List<BasicAttendanceResponseDto> getAllAttendanceService(String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();
        return response;
    }
    public  AtteandanceResponseDto getAttendancByAttendanceIdService(UUID attendanceId,String teacherId){
        if(attendanceId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        
        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                                    .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        List<Student> presentStudents = attendanceRecordRepo.findByAttendance_AttendanceIdAndStatus(attendanceId,AttendanceStatus.PRESENT);
        List<Student> absentStudents = attendanceRecordRepo.findByAttendance_AttendanceIdAndStatus(attendanceId,AttendanceStatus.ABSENT);

        AtteandanceResponseDto response = new AtteandanceResponseDto();
        response.setAttendanceId(attendance.getAttendanceId());
        response.setAttendanceDate(attendance.getAttendanceDate());
        response.setAttendanceTime(attendance.getAttendanceTime());
        response.setSubjectName(attendance.getSubjectName());
        response.setRunning(attendance.isRunning());

        response.setPresentDatas(presentStudents.stream()
                                .map(s -> {
                                    StudentResponseDto res = modelMapper.map(s, StudentResponseDto.class);
                                    res.setYear(s.getAcademic().getYear());
                                    res.setBranch(s.getAcademic().getBranch());
                                    res.setSemester(s.getAcademic().getBranch());
                                    res.setClassName(s.getAcademic().getClassName());
                                    res.setBatch(s.getAcademic().getBatch());
                                    return res;
                                })
                                .toList());

        response.setAbsentDatas(absentStudents.stream()
                                .map(s -> {
                                    StudentResponseDto res = modelMapper.map(s, StudentResponseDto.class);
                                    res.setYear(s.getAcademic().getYear());
                                    res.setBranch(s.getAcademic().getBranch());
                                    res.setSemester(s.getAcademic().getBranch());
                                    res.setClassName(s.getAcademic().getClassName());
                                    res.setBatch(s.getAcademic().getBatch());
                                    return res;
                                }).toList()
                    );

        response.setAcademicDatas(attendance.getAttendanceAcademics()
                    .stream()
                    .map(aa -> modelMapper.map(
                            aa.getAcademic(), AcademicDto.class))
                    .toList());

        return response;
    }
    
    public List<BasicAttendanceResponseDto> getAllAttendanceBySubjectNameService(String subjectName,String teacherId) {
        
        if(subjectName==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getSubjectName().equals(subjectName))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();
        return response;
    }

    public List<BasicAttendanceResponseDto> getAllAttendanceByDateService(LocalDate date,String teacherId) {
        if(date==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getAttendanceDate().equals(date))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return response;
    }

    public List<BasicAttendanceResponseDto> getAllttendanceByDateAndSubjectNameService(String subjectName,LocalDate date,String teacherId) {
        if(subjectName==null || date==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getSubjectName().equals(subjectName) && a.getAttendanceDate().equals(date))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return response;
    }

    public List<AcademicDto> getAcademicDataService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        return adminService.getAcademicDataService(teacher.getAdmin().getUserId()); // from admin service
    }

    // mark presnt student in attendance
    public void markStudentPresentInAttendanceService(UUID attendanceId,String studentId,String teacherId){
        if(attendanceId==null || studentId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
            .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, studentId)
            .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRecord.setStatus(AttendanceStatus.PRESENT);
        attendanceRecordRepo.save(attendanceRecord);

    }
    // mark absent student in attendance
    public void markStudentAbsentInAttendanceService(UUID attendanceId,String studentId,String teacherId){
        if(attendanceId==null || studentId==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
            .findByAttendance_AttendanceIdAndStudent_UserId(attendanceId, studentId)
            .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRecord.setStatus(AttendanceStatus.ABSENT);
        attendanceRecordRepo.save(attendanceRecord);
    }

}