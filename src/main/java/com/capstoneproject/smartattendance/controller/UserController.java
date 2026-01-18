package com.capstoneproject.smartattendance.controller;

import org.springframework.security.core.Authentication;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.capstoneproject.smartattendance.dto.UserDto;
import com.capstoneproject.smartattendance.service.UserService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/changepassword")
    public ResponseEntity<?> changePassword(UserDto userDto,Authentication authentication){
        String adminId = authentication.getName();
        userService.changePasswordService(userDto,adminId);
        return ResponseEntity.ok(Map.of("message", "PASSWORD_CHANGED_SUCCESSFULLY"));
    }
    
}



