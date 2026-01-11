package com.capstoneproject.smartattendance.service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.AttendanceDto;
import com.capstoneproject.smartattendance.dto.BasicAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.AttendanceStatus;
import com.capstoneproject.smartattendance.dto.BasicDataDto;
import com.capstoneproject.smartattendance.dto.StartAttendanceResponseDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;
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
import com.capstoneproject.smartattendance.util.RandomStringUtil;

@Service
public class TeacherService {

    @Autowired
    TeacherRepo teacherRepo;

    @Autowired
    AcademicRepo academicRepo;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    AttendanceRepo attendanceRepo;

    @Autowired
    AttendanceAcademicRepo attendanceAcademicRepo;

    @Autowired
    AttendanceRecordRepo attendanceRecordRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminService adminService;

    public ResponseEntity<?> getMyDetailsService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);
        return ResponseEntity.ok(basicDataDto);
    }

    public ResponseEntity<?> changePasswordService(TeacherDto teacherDto, String teacherId) {
        String password = teacherDto.getPassword();
        String newPassword = teacherDto.getNewPassword();
        String confirmPassword = teacherDto.getConfirmPassword();

        if (newPassword == null || newPassword.isBlank()) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new CustomeException(ErrorCode.BOTH_PASSWORD_SHOULD_BE_SAME);
        }
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, teacher.getPassword())) {
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }
        teacher.setPassword(passwordEncoder.encode(newPassword));
        teacherRepo.save(teacher);
        return ResponseEntity.ok(Map.of("message", "PASSWORD_CHANGED_SUCCESSFULLY"));
    }

    // create attendance
    public ResponseEntity<?> createAttendanceService(AttendanceDto attendanceDto, String teacherId) {

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

        return ResponseEntity.ok(Map.of("attendanceId",attendace.getAttendanceId()));
    }

    // start attendance
    public ResponseEntity<?> startAttendanceService(UUID attendanceId,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setRunning(true);
        attendanceRepo.save(attendance);

        StartAttendanceResponseDto response = modelMapper.map(attendance,StartAttendanceResponseDto.class);

        return ResponseEntity.ok(Map.of("response",response));
    }
    // stop attendance
    public ResponseEntity<?> stopAttendanceService(UUID attendanceId,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Attendance attendance = attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendance.setRunning(false);
        attendanceRepo.save(attendance);
        
        return ResponseEntity.ok(Map.of("message","ATTENDANCE_STOP_SUCCESSFULLY"));
    }
    // add new academic
    public ResponseEntity<?> addNewAcademicInAttendanceService(UUID attendanceId,UUID academicId,String teacherId){
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
        return ResponseEntity.ok(Map.of("message","ACADEMIC_ADDED_SUCCESSFULLY"));
    }
    // remove academic
    public ResponseEntity<?> removeAcademicInAttendanceService(UUID attendanceId,UUID academicId,String teacherId){
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

        return ResponseEntity.ok(Map.of("message","ACADEMIC_DELETED_SUCCESSFULLY"));
    }
    // delete attendance
    public ResponseEntity<?> deleteAttendanceService(UUID attendanceId,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRepo.deleteById(attendanceId);
        
        return ResponseEntity.ok(Map.of("message","ATTENDANCE_DELETE_SUCCESSFULLY"));
    }
    // get all attendance
    public ResponseEntity<?> getAllAttendanceService(String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return ResponseEntity.ok(Map.of("response",response));
    }
    
    public ResponseEntity<?> getAllAttendanceBySubjectNameService(String subjectName,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getSubjectName().equals(subjectName))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return ResponseEntity.ok(Map.of("response",response));
    }

    public ResponseEntity<?> getAllAttendanceByDateService(LocalDate date,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getAttendanceDate().equals(date))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return ResponseEntity.ok(Map.of("response",response));
    }

    public ResponseEntity<?> getAllttendanceByDateAndSubjectNameService(String subjectName,LocalDate date,String teacherId) {
        
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Attendance> attendances = attendanceRepo.findByTeacher_UserId(teacherId);

        List<BasicAttendanceResponseDto> response= attendances
                            .stream()
                            .filter(a -> a.getSubjectName().equals(subjectName) && a.getAttendanceDate().equals(date))
                            .map(a->modelMapper.map(a, BasicAttendanceResponseDto.class))
                            .toList();

        return ResponseEntity.ok(Map.of("response",response));
    }

    public ResponseEntity<?> getAcademicDataService(String teacherId) {
        Teacher teacher = teacherRepo.findById(teacherId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        return adminService.getAcademicDataService(teacher.getAdmin().getUserId()); // from admin service
    }

    // mark presnt student in attendance
    public ResponseEntity<?> markStudentPresentInAttendance(UUID attendanceId,String studentId,String teacherId){
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
            .findByAttendance_AttendanceIdAndStudent_StudentId(attendanceId, studentId)
            .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRecord.setStatus(AttendanceStatus.PRESENT);
        attendanceRecordRepo.save(attendanceRecord);

        return ResponseEntity.ok(Map.of("message","STUDENT_ADDED_SUCCESSFULLY"));

    }
    // mark absent student in attendance
    public ResponseEntity<?> markStudentAbsentInAttendance(UUID attendanceId,String studentId,String teacherId){
        teacherRepo.findById(teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        attendanceRepo.findByAttendanceIdAndTeacher_UserId(attendanceId, teacherId)
                        .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        AttendanceRecord attendanceRecord = attendanceRecordRepo
            .findByAttendance_AttendanceIdAndStudent_StudentId(attendanceId, studentId)
            .orElseThrow(() -> new CustomeException(ErrorCode.ATTENDANCE_RECORD_NOT_FOUND));

        attendanceRecord.setStatus(AttendanceStatus.ABSENT);
        attendanceRecordRepo.save(attendanceRecord);

        return ResponseEntity.ok(Map.of("message","STUDENT_REMOVED_SUCCESSFULLY"));
    }

}