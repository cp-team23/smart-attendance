package com.capstoneproject.smartattendance.service.mail;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MailSenderService {

    
    private final JavaMailSender mailSender;

    public void sendMail(String to, String subject, String body) {

        if (mailSender == null) {
            throw new CustomeException(ErrorCode.MAIL_SERVICE_NOT_AVAILABLE);
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);

            mailSender.send(message);

        } catch (CustomeException e) {
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }
}
