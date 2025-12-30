package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.capstoneproject.smartattendance.dto.StudentDto;
import com.capstoneproject.smartattendance.service.AdminService;


@RestController
@RequestMapping("admin")
public class AdminController {
     
    @Autowired
    AdminService adminService;

    @PostMapping("/addstudent")
    public ResponseEntity<?> addStudent(@RequestBody StudentDto studentDto,Authentication authentication){
        String adminName = authentication.getName();
        return adminService.addStudentService(studentDto,adminName);  
    }

    // @PostMapping("/updatestudent")
    // public ResponseEntity<?> updatestudent(@RequestBody StudentDto studentDto,Authentication authentication){
    //     String adminName = authentication.getName();
    //     return adminService.updateStudentService(studentDto,adminName);
    // }

}
