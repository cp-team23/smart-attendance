package com.capstoneproject.smartattendance.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.OtpDto;
import com.capstoneproject.smartattendance.exception.AuthException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

@Service
public class OtpService {

    @Autowired
    MailService mailService;

    private final Map<String, OtpDto> otpStore = new ConcurrentHashMap<>();

    public ResponseEntity<?> createOtp(String email) {

        long expiresTime = Instant.now().plusSeconds(2 * 60).toEpochMilli(); // 2 min
        int code = ThreadLocalRandom.current().nextInt(100000, 1_000_000);
        String otp = String.valueOf(code);

        try {
            ResponseEntity<?> response = mailService.sendOtpOnMail(email, otp);
            otpStore.put(email, new OtpDto(otp, expiresTime));
            return response;

        } catch (Exception e) {
            throw new AuthException(ErrorCode.INTERNAL_ERROR);
        }
    }

    public void verifyOtp(String email, String otp) {
        OtpDto otpDto = otpStore.get(email);

        if (otpDto == null) {
            throw new AuthException(ErrorCode.NO_OTP_RECORD);
        }

        if (Instant.now().toEpochMilli() > otpDto.getExpireTime()) {
            otpStore.remove(email);
            throw new AuthException(ErrorCode.OTP_EXPIRED);
        }
        if (!otpDto.getOtp().equals(otp)) {
            throw new AuthException(ErrorCode.INVALID_OTP);
        }
        otpStore.remove(email);
    }

    @Scheduled(fixedRate = 5 * 60 * 1000) // every 5 minutes
    public void cleanExpiredOtps() {
        long now = System.currentTimeMillis();

        otpStore.entrySet().removeIf(entry -> entry.getValue().getExpireTime() < now);
    }

}
