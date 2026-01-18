package com.capstoneproject.smartattendance.service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.modelmapper.ModelMapper;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import com.capstoneproject.smartattendance.dto.AdminDto;
import com.capstoneproject.smartattendance.dto.Role;
import com.capstoneproject.smartattendance.dto.UserDto;
import com.capstoneproject.smartattendance.entity.Admin;
import com.capstoneproject.smartattendance.entity.User;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.AdminRepo;
import com.capstoneproject.smartattendance.repository.UserRepo;
import com.capstoneproject.smartattendance.security.HashUtil;
import com.capstoneproject.smartattendance.security.JwtService;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;

    private final PasswordEncoder passwordEncoder;

    private final ModelMapper modelMapper;

    private final StringRedisTemplate redisTemplate;

    private final AdminRepo adminRepo;

    private final UserRepo userRepo;

    private final JwtService jwtService;

    private static final long JWT_TTL = 60 * 60 * 24 * 7;

    // admin register service
    public void adminRegister(AdminDto adminDto) {

        String userId = adminDto.getUserId();
        String email = adminDto.getEmail().toLowerCase();
        String otp = adminDto.getOtp();
        String password = adminDto.getPassword();
        String confirmPassword = adminDto.getConfirmPassword();
        adminDto.setRole(Role.ADMIN);

        if (!password.equals(confirmPassword)) {
            throw new CustomeException(ErrorCode.BOTH_PASSWORD_SHOULD_BE_SAME);
        }
        if (adminRepo.findById(userId).isPresent()) {
            throw new CustomeException(ErrorCode.USERID_NOT_AVAILABLE);
        }
        if (adminRepo.findByEmail(email).isPresent()) {
            throw new CustomeException(ErrorCode.EMAIL_NOT_AVAILABLE);
        }

        otpService.verifyOtp(email, otp);

        adminDto.setPassword(passwordEncoder.encode(password));
        Admin admin = modelMapper.map(adminDto, Admin.class);
        adminRepo.save(admin);

    }

    // otp send service
    public void sendOtpService(String email) {
        try {
            if (email == null || email.isBlank()) {
                throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
            }
            otpService.createOtp(email.toLowerCase());
        } catch (CustomeException e) {
            throw new CustomeException(ErrorCode.INTERNAL_ERROR);
        }
    }

    // all user login service
    public ResponseEntity<?> loginService(UserDto userDto, HttpServletResponse response) {

        String userId = userDto.getUserId();
        String password = userDto.getPassword();
        Role role = userDto.getRole();

        if (userId == null || userId.isBlank() || password == null || password.isBlank()) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }

        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!user.getRole().equals(role)) {
            throw new CustomeException(ErrorCode.USER_NOT_FOUND); // SAME MESSAGE AS BEFORE
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }

        String token = jwtService.generateToken(user.getUserId(), user.getRole());
        String tokenHash = HashUtil.sha256(token);

        redisTemplate.opsForValue()
                     .set("jwt:" + userId, tokenHash, JWT_TTL, TimeUnit.SECONDS);
                     
        Map<String, Object> responseBody = new HashMap<>();
        responseBody.put("token", token);
        responseBody.put("username", user.getUserId());
        responseBody.put("role", user.getRole());

        Cookie jwtCookie = new Cookie("JWT", token);
        jwtCookie.setHttpOnly(true);
        jwtCookie.setPath("/");
        jwtCookie.setMaxAge(24 * 60 * 60);
        jwtCookie.setSecure(false);
        response.addCookie(jwtCookie);

        return ResponseEntity.ok(responseBody);
    }

    // all user logout service
    public void logoutService(HttpServletResponse response,String userId) {

        Cookie cookie = new Cookie("JWT", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setSecure(false);

        response.addCookie(cookie);
        redisTemplate.delete("jwt:" + userId);
    }

    // admin forgot password service
    public void forgotPasswordService(AdminDto adminDto) {

        String userId = adminDto.getUserId();
        String email = adminDto.getEmail().toLowerCase();
        String otp = adminDto.getOtp();
        String password = adminDto.getPassword();
        String confirmPassword = adminDto.getConfirmPassword();

        if(otp==null || confirmPassword==null){
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (!password.equals(confirmPassword)) {
            throw new CustomeException(ErrorCode.BOTH_PASSWORD_SHOULD_BE_SAME);
        }

        Admin admin = adminRepo.findById(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!admin.getEmail().equals(adminDto.getEmail())) {
            throw new CustomeException(ErrorCode.EMAIL_NOT_MATCH);
        }

        otpService.verifyOtp(email, otp);

        admin.setPassword(passwordEncoder.encode(password));
        adminRepo.save(admin);
    }

}