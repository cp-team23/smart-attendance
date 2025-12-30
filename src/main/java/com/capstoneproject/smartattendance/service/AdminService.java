package com.capstoneproject.smartattendance.service;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.Role;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.exception.AuthException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.StudentRepository;
import com.capstoneproject.smartattendance.service.mail.AdminMailService;

@Service
public class AdminService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminMailService adminMailService;

    public ResponseEntity<?> addStudentService(StudentDto studentDto, String adminName) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();
        String name = studentDto.getName();
        String collegeName = studentDto.getCollegeName();
        String departmentName = studentDto.getDepartmentName();
        String enrollmentNo = studentDto.getEnrollmentNo();
        String sem = studentDto.getSem();
        String email = studentDto.getEmail();
        String className = studentDto.getClassName();
        String batchName = studentDto.getBatchName();

        if (userId == null || userId.isBlank() || password == null || password.isBlank() ||
                name == null || name.isBlank() || collegeName == null || collegeName.isBlank() ||
                departmentName == null || departmentName.isBlank() || enrollmentNo == null || enrollmentNo.isBlank() ||
                sem == null || sem.isBlank() || email == null || email.isBlank() ||
                className == null || className.isBlank() || batchName == null || batchName.isBlank()) {

            throw new AuthException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        if (studentRepository.findById(userId).isPresent()) {
            throw new AuthException(ErrorCode.USERID_NOT_AVAILABLE);
        }

        Student student = modelMapper.map(studentDto, Student.class);
        student.setAttendance(0);
        student.setRole(Role.STUDENT);
        student.setManagedBy(adminName);
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentAccountCreatedMail(studentDto, adminName);
        studentRepository.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ID_CREATED_SUCCESSFULLY"));
    }

    // public ResponseEntity<?> updateStudentService(StudentDto studentDto,String adminId){

    // }

}
