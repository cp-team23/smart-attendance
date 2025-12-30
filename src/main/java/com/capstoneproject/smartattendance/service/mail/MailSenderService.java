package com.capstoneproject.smartattendance.service.mail;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.exception.AuthException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

@Service
public class MailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body) {

        if (mailSender == null) {
            throw new AuthException(ErrorCode.MAIL_SERVICE_NOT_AVAILABLE);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

        } catch (Exception e) {
            throw new AuthException(ErrorCode.MAIL_SEND_FAILED);
        }
    }
}
