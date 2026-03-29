package com.capstoneproject.smartattendance.service.mail;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailSenderService {

    private final JavaMailSender mailSender;

    @Async
    public void sendMail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom("smartattendanceprojectteam@gmail.com");
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    public void sendMailSync(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setFrom("smartattendanceprojectteam@gmail.com");
            helper.setSubject(subject);
            helper.setText(body, true);

            mailSender.send(message);
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }
}




// package com.capstoneproject.smartattendance.service.mail;

// import jakarta.mail.internet.MimeMessage;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import org.springframework.scheduling.annotation.Async;
// import org.springframework.stereotype.Service;

// import com.capstoneproject.smartattendance.exception.CustomeException;
// import com.capstoneproject.smartattendance.exception.ErrorCode;

// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class MailSenderService {

//     private final JavaMailSender mailSender;

//     @Async
//     public void sendMail(String to, String subject, String body) {

//         try {
//             MimeMessage message = mailSender.createMimeMessage();
//             MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

//             helper.setTo(to);
//             helper.setSubject(subject);
//             helper.setText(body, true);   // true = isHtml

//             mailSender.send(message);

//         } catch (Exception e) {
//             log.error("Failed to send mail to {}: {}", to, e.getMessage());
//             throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
//         }
//     }
// }