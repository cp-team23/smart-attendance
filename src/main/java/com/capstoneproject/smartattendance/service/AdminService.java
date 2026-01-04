package com.capstoneproject.smartattendance.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
import com.capstoneproject.smartattendance.repository.StudentRepo;
import com.capstoneproject.smartattendance.repository.TeacherRepo;
import com.capstoneproject.smartattendance.repository.UserRepo;
import com.capstoneproject.smartattendance.service.mail.AdminMailService;

import jakarta.transaction.Transactional;

@Service
public class AdminService {

    @Autowired
    UserRepo userRepo;

    @Autowired
    StudentRepo studentRepo;

    @Autowired
    AdminRepo adminRepo;

    @Autowired
    TeacherRepo teacherRepo;

    @Autowired
    AcademicRepo academicRepo;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminMailService adminMailService;

    @Autowired
    OtpService otpService;

    public ResponseEntity<?> getAcademicDataService(String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<AcademicDto> response = academicRepo.findByAdminUserId(adminId)
                .stream()
                .map(a -> new AcademicDto(
                        a.getBranch(),
                        a.getSemester(),
                        a.getClassName(),
                        a.getBatch()))
                .toList();

        return ResponseEntity.ok(Map.of("academicDatas", response));
    }

    public ResponseEntity<?> updateAcademicDataService(AdminDto adminDto, String adminId) {
        deleteAcademicDataService(adminId);

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<Academic> academics = new ArrayList<>();

        for (AcademicDto dto : adminDto.getAcademicDatas()) {

            Academic academic = new Academic();
            academic.setBranch(dto.getBranch());
            academic.setSemester(dto.getSemester());
            academic.setClassName(dto.getClassName());
            academic.setBatch(dto.getBatch());

            academic.setAdmin(admin);

            academics.add(academic);
        }

        admin.setAcademicDatas(academics);
        adminRepo.save(admin);

        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }

    @Transactional
    public ResponseEntity<?> deleteAcademicDataService(String adminId) {
        academicRepo.deleteByAdminUserId(adminId);
        return ResponseEntity.ok(Map.of("message", "DELETED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> getMyDetailsService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        BasicDataDto basicDataDto = modelMapper.map(admin, BasicDataDto.class);
        return ResponseEntity.ok(basicDataDto);
    }

    public ResponseEntity<?> updateAdminService(AdminDto adminDto, String adminId) {
        String collegeName = adminDto.getCollegeName();
        String name = adminDto.getName();
        String otp = adminDto.getOtp();
        String email = adminDto.getEmail();
        String password = adminDto.getPassword();

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!admin.getEmail().equals(email)) {
            if (otp == null) {
                throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
            }
            otpService.verifyOtp(email, otp);
            admin.setEmail(email);
        }
        admin.setName(name);
        admin.setCollegeName(collegeName);
        admin.setPassword(passwordEncoder.encode(password));

        adminRepo.save(admin);

        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> addStudentService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();

        if (userRepo.findById(userId).isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = modelMapper.map(studentDto, Student.class);

        student.setAttendance(0);
        student.setRole(Role.STUDENT);
        student.setAdmin(admin);
        student.setCollegeName(admin.getCollegeName());
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(studentDto, adminId, "created");
        studentRepo.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateStudentService(StudentDto studentDto, String adminId) {
        String userId = studentDto.getUserId();

        Student student = studentRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!student.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        student.setName(studentDto.getName());
        student.setCollegeName(admin.getCollegeName());
        student.setDepartmentName(studentDto.getDepartmentName());
        student.setEnrollmentNo(studentDto.getEnrollmentNo());
        student.setSem(studentDto.getSem());
        student.setEmail(studentDto.getEmail());
        student.setClassName(studentDto.getClassName());
        student.setBatchName(studentDto.getBatchName());
        student.setPassword(passwordEncoder.encode(studentDto.getPassword()));

        adminMailService.sendStudentDetailsMail(studentDto, adminId, "updated");
        studentRepo.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> deleteStudentService(String userId, String adminId) {

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
        studentRepo.deleteById(userId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_DELETED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> addTeacherService(TeacherDto teacherDto, String adminId) {
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

        adminMailService.sendTeacherDetailsMail(teacherDto, adminId, "created");
        teacherRepo.save(teacher);
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateTeacherService(TeacherDto teacherDto, String adminId) {
        String userId = teacherDto.getUserId();
        String password = teacherDto.getPassword();

        Teacher teacher = teacherRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!teacher.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        teacher.setName(teacherDto.getName());
        teacher.setEmail(teacherDto.getEmail());
        teacher.setPassword(passwordEncoder.encode(password));

        adminMailService.sendTeacherDetailsMail(teacherDto, adminId, "updated");
        teacherRepo.save(teacher);
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> deleteTeacherService(String userId, String adminId) {

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
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_DELETED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> searchStudentService(String userId, String adminId) {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = studentRepo.findByUserIdAndAdmin_UserId(userId, adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        StudentResponseDto studentResponseDto = modelMapper.map(student, StudentResponseDto.class);

        return ResponseEntity.ok(Map.of("response", studentResponseDto));
    }

    public ResponseEntity<?> searchTeacherService(String userId, String adminId) {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = teacherRepo.findByUserIdAndAdmin_UserId(userId, adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);

        return ResponseEntity.ok(Map.of("response", basicDataDto));
    }

    public ResponseEntity<?> getAllStudentService(String adminId) {

        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        // List<Student> students = ;

        List<StudentResponseDto> response = studentRepo.findByAdminUserId(adminId)
                                        .stream()
                                        .map(a->modelMapper.map(a,StudentResponseDto.class))
                                        .toList();

        return ResponseEntity.ok(Map.of("response", response));
    }

    public ResponseEntity<?> getAllteacherService(String adminId) {
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));


        List<BasicDataDto> response = teacherRepo.findByAdminUserId(adminId)
                                      .stream()
                                      .map(a->modelMapper.map(a,BasicDataDto.class))
                                      .toList();

        return ResponseEntity.ok(Map.of("response", response));

    }

}
