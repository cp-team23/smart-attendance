package com.capstoneproject.smartattendance.service.mail;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.capstoneproject.smartattendance.exception.CustomeException;
import com.capstoneproject.smartattendance.exception.ErrorCode;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MailSenderService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendMail(String to, String subject, String body) {
        try {
            Map<String, Object> payload = Map.of(
                "sender", Map.of("email", "smartattendanceprojectteam@gmail.com", "name", "Smart Attendance"),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", body
            );

            restTemplate.postForObject(
                "https://api.brevo.com/v3/smtp/email",
                new HttpEntity<>(payload, buildHeaders()),
                String.class
            );

            log.info("Mail sent to {}: {}", to, subject);
        } catch (Exception e) {
            log.error("Failed to send mail to {}: {}", to, e.getMessage());
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    @Async
    public void sendMailWithAttachment(String to, String subject, String body,
                                       byte[] attachmentBytes, String attachmentName,
                                       String contentType) {
        try {
            Map<String, Object> payload = Map.of(
                "sender", Map.of("email", "smartattendanceprojectteam@gmail.com", "name", "Smart Attendance"),
                "to", List.of(Map.of("email", to)),
                "subject", subject,
                "htmlContent", body,
                "attachment", List.of(Map.of(
                    "content", Base64.getEncoder().encodeToString(attachmentBytes),
                    "name", attachmentName
                ))
            );

            restTemplate.postForObject(
                "https://api.brevo.com/v3/smtp/email",
                new HttpEntity<>(payload, buildHeaders()),
                String.class
            );

            log.info("Mail with attachment '{}' sent to {}: {}", attachmentName, to, subject);
        } catch (Exception e) {
            log.error("Failed to send mail with attachment to {}: {}", to, e.getMessage());
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }

    private HttpHeaders buildHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", brevoApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}