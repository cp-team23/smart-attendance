package com.capstoneproject.smartattendance.service;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;
import com.capstoneproject.smartattendance.service.mail.AuthMailService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OtpService {

   
    private final StringRedisTemplate redisTemplate;

    private final AuthMailService authMailService;

    private static final long OTP_TTL_SECONDS = 120;

    public void createOtp(String email) {

        int code = ThreadLocalRandom.current().nextInt(100000, 1_000_000);
        String otp = String.valueOf(code);

        authMailService.sendOtpMail(email, otp);

        redisTemplate.opsForValue()
                .set("otp:" + email, otp, OTP_TTL_SECONDS, TimeUnit.SECONDS);
    }

    public void verifyOtp(String email, String otp) {

        String key = "otp:" + email;
        String savedOtp = redisTemplate.opsForValue().get(key);

        if (savedOtp == null) {
            throw new CustomeException(ErrorCode.OTP_EXPIRED);
        }

        if (!savedOtp.equals(otp)) {
            throw new CustomeException(ErrorCode.INVALID_OTP);
        }

        redisTemplate.delete(key);
    }
}
