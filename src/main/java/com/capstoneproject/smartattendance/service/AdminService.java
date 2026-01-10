package com.capstoneproject.smartattendance.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<AcademicDto> response = admin.getAcademicDatas()
                .stream()
                .map(a -> modelMapper.map(a, AcademicDto.class))
                .toList();

        return ResponseEntity.ok(Map.of("academicDatas", response));
    }

    public ResponseEntity<?> createAcademicDataService(AcademicDto academicDto, String adminId) {
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
            throw new CustomeException(ErrorCode.SAME_STRUCTURE_EXIST_ALREADY);
        }

        academic.setAdmin(admin);

        admin.getAcademicDatas().add(academic);
        adminRepo.save(admin);  

        return ResponseEntity.ok(Map.of("message", "CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateAcademicDataService(AcademicDto academicDto, String adminId) {
        UUID academicId = academicDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

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
            throw new CustomeException(ErrorCode.SAME_STRUCTURE_EXIST_ALREADY);
        }
        academic.setAdmin(admin);
        admin.getAcademicDatas().add(academic);

        adminRepo.save(admin);
        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }

    @Transactional
    public ResponseEntity<?> deleteAcademicDataService(AcademicDto academicDto, String adminId) {
        UUID academicId = academicDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

        if (academic.getStudents().size() > 0) {
            throw new CustomeException(ErrorCode.CANT_DELETE_THIS);
        }
        academicRepo.deleteById(academicId);
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

        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> addStudentService(StudentDto studentDto,String adminId) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();
        UUID academicId = studentDto.getAcademicId();
        if (academicId == null) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (userRepo.findById(userId).isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Admin admin = adminRepo.findById(adminId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Academic academic = academicRepo.findById(academicId)
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

        if (!academic.getAdmin().getUserId().equals(adminId)) {
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        Student student = modelMapper.map(studentDto, Student.class);

        student.setAttendance(0);
        student.setRole(Role.STUDENT);
        student.setAdmin(admin);
        student.setCollegeName(admin.getCollegeName());
        student.setAcademic(academic);
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(student, adminId, "created");
        studentRepo.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateStudentService(StudentDto studentDto, String adminId) {
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
                .orElseThrow(() -> new CustomeException(ErrorCode.ACADEMIC_DETAILS_NOT_FOUND));

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

        adminMailService.sendTeacherDetailsMail(teacher, adminId, "created");
        teacherRepo.save(teacher);
        return ResponseEntity.ok(Map.of("message", "TEACHER_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateTeacherService(TeacherDto teacherDto, String adminId) {
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

    public ResponseEntity<?> getStudentService(String userId, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Student student = admin.getStudents()
                .stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        Academic academic = student.getAcademic();
        StudentResponseDto studentResponseDto = modelMapper.map(student, StudentResponseDto.class);

        studentResponseDto.setYear(academic.getYear());
        studentResponseDto.setBranch(academic.getBranch());
        studentResponseDto.setSemester(academic.getSemester());
        studentResponseDto.setClassName(academic.getClassName());
        studentResponseDto.setBatch(academic.getBatch());

        return ResponseEntity.ok(Map.of("response", studentResponseDto));
    }

    public ResponseEntity<?> getTeacherService(String userId, String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        Teacher teacher = admin.getTeachers()
                .stream()
                .filter(a -> a.getUserId().equals(userId))
                .findFirst()
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        BasicDataDto basicDataDto = modelMapper.map(teacher, BasicDataDto.class);

        return ResponseEntity.ok(Map.of("response", basicDataDto));
    }

    public ResponseEntity<?> getAllStudentService(String adminId) {

        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<StudentResponseDto> response = admin.getStudents()
                .stream()
                .map(a -> {
                        StudentResponseDto studentResponseDto = modelMapper.map(a, StudentResponseDto.class);
                        studentResponseDto.setYear(a.getAcademic().getYear());
                        studentResponseDto.setBranch(a.getAcademic().getBranch());
                        studentResponseDto.setSemester(a.getAcademic().getSemester());
                        studentResponseDto.setClassName(a.getAcademic().getClassName());
                        studentResponseDto.setBatch(a.getAcademic().getBatch());

                        return studentResponseDto;
                    })
                .toList();

        return ResponseEntity.ok(Map.of("response", response));
    }

    public ResponseEntity<?> getAllteacherService(String adminId) {
        Admin admin = adminRepo.findById(adminId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        List<BasicDataDto> response = admin.getTeachers()
                .stream()
                .map(a -> modelMapper.map(a, BasicDataDto.class))
                .toList();

        return ResponseEntity.ok(Map.of("response", response));

    }

    public ResponseEntity<?> changePasswordService(AdminDto adminDto,String adminId){
        String password = adminDto.getPassword();
        String newPassword = adminDto.getNewPassword();
        String confirmPassword = adminDto.getConfirmPassword();

        if(newPassword==null || newPassword.isBlank()){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if(!newPassword.equals(confirmPassword)){
            throw new CustomeException(ErrorCode.BOTH_PASSWORD_SHOULD_BE_SAME);
        }
        Admin admin = adminRepo.findById(adminId).orElseThrow(()->new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, admin.getPassword())) {
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }
        admin.setPassword(passwordEncoder.encode(newPassword));
        adminRepo.save(admin);
        return ResponseEntity.ok(Map.of("message","PASSWORD_CHANGED_SUCCESSFULLY"));
    }

}
