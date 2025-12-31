package com.capstoneproject.smartattendance.service;

import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.Role;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.Student;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.AdminRepository;
import com.capstoneproject.smartattendance.repository.StudentRepository;
import com.capstoneproject.smartattendance.service.mail.AdminMailService;

@Service
public class AdminService {

    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AdminRepository adminRepository;

    @Autowired
    ModelMapper modelMapper;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    AdminMailService adminMailService;

    public ResponseEntity<?> updateAcademicStructure(String academicstructure, String adminName) {
        Admin admin = adminRepository.findById(adminName).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        admin.setAcademicStructure(academicstructure);
        adminRepository.save(admin);
        return ResponseEntity.ok(Map.of("message", "UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> addStudentService(StudentDto studentDto, String adminName) {
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();

        studentRepository.findById(userId).orElseThrow(() -> new CustomeException(ErrorCode.USERID_NOT_AVAILABLE));
        

        Student student = modelMapper.map(studentDto, Student.class);
        student.setAttendance(0);
        student.setRole(Role.STUDENT);
        student.setManagedBy(adminName);
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(studentDto, adminName,"created");
        studentRepository.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_CREATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> updateStudentService(StudentDto studentDto,String adminName){
        String userId = studentDto.getUserId();
        String password = studentDto.getPassword();

        Student prevStudent = studentRepository.findById(userId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));
        
        if(!prevStudent.getManagedBy().equals(adminName)){
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }

        Student student = modelMapper.map(studentDto, Student.class);
        student.setAttendance(prevStudent.getAttendance());
        student.setRole(Role.STUDENT);
        student.setManagedBy(adminName);
        student.setPassword(passwordEncoder.encode(password));

        adminMailService.sendStudentDetailsMail(studentDto, adminName,"updated");
        studentRepository.save(student);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_UPDATED_SUCCESSFULLY"));
    }

    public ResponseEntity<?> deleteStudentService(String userId,String adminName){
        
        Student student = studentRepository.findById(userId).orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if(!student.getManagedBy().equals(adminName)){
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        if(!student.getRole().equals(Role.STUDENT)){
            throw new CustomeException(ErrorCode.NOT_ALLOWED);
        }
        studentRepository.deleteById(userId);
        return ResponseEntity.ok(Map.of("message", "STUDENT_ACCOUNT_DELETED_SUCCESSFULLY"));

    }
}
