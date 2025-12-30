package com.capstoneproject.smartattendance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.UserDto;
import com.capstoneproject.smartattendance.service.AuthService;
import com.capstoneproject.smartattendance.service.OtpService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {

     @Autowired
     AuthService authService;

     @Autowired 
     OtpService otpService;

     @PostMapping("/login")
     public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto,HttpServletResponse response){
            return authService.loginService(userDto,response);
     }

     @PostMapping("/register")
     public ResponseEntity<?> register(@Valid @RequestBody AdminDto adminDto){
            return authService.adminRegister(adminDto);
     }

     @PostMapping("/sendotp")
     public ResponseEntity<?> sendOtp(@RequestBody AdminDto adminDto) {
            String email = adminDto.getEmail();
            return authService.sendOtpService(email);
     }

     @PostMapping("/logout")
     public ResponseEntity<?> login(HttpServletResponse response){
            return authService.logoutService(response);
     }

     @PostMapping("/forgotpassword")
     public ResponseEntity<?> forgotPassword(@RequestBody AdminDto adminDto){
            return authService.forgotPasswordService(adminDto);
     }

}
