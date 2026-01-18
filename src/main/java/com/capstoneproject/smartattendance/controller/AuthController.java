package com.capstoneproject.smartattendance.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.UserDto;
import com.capstoneproject.smartattendance.service.AuthService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
public class AuthController {

     private final AuthService authService;


     @PostMapping("/login")
     public ResponseEntity<?> login(@Valid @RequestBody UserDto userDto,HttpServletResponse response){
           return authService.loginService(userDto,response);
     }

     @PostMapping("/register")
     public ResponseEntity<?> register(@Valid @RequestBody AdminDto adminDto){
            authService.adminRegister(adminDto);
            return ResponseEntity.ok(Map.of("message", "REGISTER_SUCCESSFULLY"));
     }

     @PostMapping("/sendotp")
     public ResponseEntity<?> sendOtp(@RequestBody AdminDto adminDto) {
            String email = adminDto.getEmail();
            authService.sendOtpService(email);
            return ResponseEntity.ok(Map.of("message", "OTP_SENT"));
     }

     @PostMapping("/logout")
     public ResponseEntity<?> login(HttpServletResponse response,Authentication authentication){
        String userId = authentication.getName();    
        authService.logoutService(response,userId);
        return ResponseEntity.ok(Map.of("message", "LOGGED_OUT"));
     }

     @PostMapping("/forgotpassword")
     public ResponseEntity<?> forgotPassword(@Valid @RequestBody AdminDto adminDto){
           authService.forgotPasswordService(adminDto);
           return ResponseEntity.ok(Map.of("message", "PASSWORD_CHANGE_SUCCESSFULLY"));

     }

}
