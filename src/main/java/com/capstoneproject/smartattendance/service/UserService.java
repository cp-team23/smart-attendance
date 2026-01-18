package com.capstoneproject.smartattendance.service;



import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.UserDto;
import com.capstoneproject.smartattendance.entity.User;
import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.repository.UserRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
    
    private final UserRepo userRepo;

    private final PasswordEncoder passwordEncoder;

     public void  changePasswordService(UserDto userDto, String userId) {
        String password = userDto.getPassword();
        String newPassword = userDto.getNewPassword();
        String confirmPassword = userDto.getConfirmPassword();

        if (newPassword == null || newPassword.isBlank()) {
            throw new CustomeException(ErrorCode.ALL_FIELD_REQUIRED);
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new CustomeException(ErrorCode.BOTH_PASSWORD_SHOULD_BE_SAME);
        }
        User user = userRepo.findByUserId(userId)
                .orElseThrow(() -> new CustomeException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new CustomeException(ErrorCode.WRONG_PASSWORD);
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepo.save(user);
    }
}
