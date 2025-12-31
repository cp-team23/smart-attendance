package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.service.AdminService;

import jakarta.validation.Valid;


@RestController
@RequestMapping("admin")
public class AdminController {
     
    @Autowired
    AdminService adminService;

    @PostMapping("/addstudent")
    public ResponseEntity<?> addStudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminName = authentication.getName();
        return adminService.addStudentService(studentDto,adminName);  
    }

    @PutMapping("/updatestudent")
    public ResponseEntity<?> updatestudent(@Valid @RequestBody StudentDto studentDto,Authentication authentication){
        String adminName = authentication.getName();
        return adminService.updateStudentService(studentDto,adminName);
    }

    @PutMapping("/updateacademicstructure")
    public ResponseEntity<?> updateacademicstructure(@RequestBody String academicstructure,Authentication authentication){
        String adminName = authentication.getName();
        return adminService.updateAcademicStructure(academicstructure,adminName);
    }

    @DeleteMapping("/deletestudent/{userId}")
    public ResponseEntity<?> deleteStudent(@PathVariable String userId,Authentication authentication){
        String adminName = authentication.getName();
        return adminService.deleteStudentService(userId,adminName);
    }
    

}
