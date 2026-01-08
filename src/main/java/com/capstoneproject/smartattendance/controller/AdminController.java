package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AcademicDto;
import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.dto.TeacherDto;
import com.capstoneproject.smartattendance.service.AdminService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("api/admin")
public class AdminController {
     
    @Autowired
    AdminService adminService;

    @GetMapping("/my")
    public ResponseEntity<?> getMyDetails(Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getMyDetailsService(adminId);
    }
    
    @PostMapping("/createacademicstructure")
    public ResponseEntity<?> createAcademicStructure(@Valid @RequestBody AcademicDto academicDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.createAcademicDataService(academicDto,adminId);
    }
    
    @PutMapping("/updateacademicstructure")
    public ResponseEntity<?> updateAcademicStructure(@Valid @RequestBody AcademicDto academicDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.updateAcademicDataService(academicDto,adminId);
    }
    @GetMapping("/getacademicstructure")
    public ResponseEntity<?> getAcademicStructure(Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getAcademicDataService(adminId);
    }
    @DeleteMapping("/deleteacademicstructure")
    public ResponseEntity<?> deleteAcademicStructure(@RequestBody AcademicDto academicDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.deleteAcademicDataService(academicDto,adminId);
    }

    @PutMapping("/updateadmin")
    public ResponseEntity<?> updateAdmin(@Valid @RequestBody AdminDto adminDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.updateAdminService(adminDto,adminId);
    }
    @PutMapping("/changepassword")
    public ResponseEntity<?> changePassword(AdminDto adminDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.changePasswordService(adminDto,adminId);
    }

    @PostMapping("/addstudent")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.addStudentService(studentDto,adminId);  
    }

    @PutMapping("/updatestudent")
    public ResponseEntity<?> updatestudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.updateStudentService(studentDto,adminId);
    }

    @DeleteMapping("/deletestudent/{userId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.deleteStudentService(userId,adminId);
    }

    @PostMapping("/addteacher")
    public ResponseEntity<?> addTeacher(@Valid @RequestBody TeacherDto teacherDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.addTeacherService(teacherDto,adminId);  
    }

    @PutMapping("/updateteacher")
    public ResponseEntity<?> updateTeacher(@Valid @RequestBody TeacherDto teacherDto,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.updateTeacherService(teacherDto,adminId);
    }

    @DeleteMapping("/deleteteacher/{userId}")
    public ResponseEntity<?> deleteTeacher(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.deleteTeacherService(userId,adminId);
    }

    @GetMapping("/student/{userId}")
    public ResponseEntity<?> getStudent(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getStudentService(userId,adminId);
    }
    
    @GetMapping("/teacher/{userId}")
    public ResponseEntity<?> getTeacher(@PathVariable String userId,Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getTeacherService(userId,adminId);
    }

    @GetMapping("/allstudent")
    public ResponseEntity<?> getAllStudent(Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getAllStudentService(adminId);
    }
    @GetMapping("/allteacher")
    public ResponseEntity<?> getAllTeacher(Authentication authentication){
        String adminId = authentication.getName();
        return adminService.getAllteacherService(adminId);
    }

}
