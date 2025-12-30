package com.capstoneproject.smartattendance.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthMailService {

    @Autowired
    private MailSenderService mailSenderService;

    public void sendOtpMail(String email, String otp) {

        String subject = "Your One-Time Password (OTP) for Account Verification";

        String body =
                "Dear User,\n\n" +
                "Your One-Time Password (OTP) is: " + otp + "\n\n" +
                "This OTP is valid for 2 minutes.\n" +
                "If you did not request this, please ignore this email.\n\n" +
                "Best regards,\n" +
                "Smart Attendance Team";

        mailSenderService.sendMail(email, subject, body);
    }
}
