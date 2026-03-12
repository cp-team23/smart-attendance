package com.capstoneproject.smartattendance.service.mail;

import java.util.HashMap;
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

@Service
public class MailSenderService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    @Async
    public void sendMail(String to, String subject, String body) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "Bearer " + resendApiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);

            Map<String, Object> payload = new HashMap<>();
            payload.put("from", "onboarding@resend.dev"); // free tier default sender
            payload.put("to", to);
            payload.put("subject", subject);
            payload.put("html", body);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForObject("https://api.resend.com/emails", entity, String.class);

        } catch (Exception e) {
            throw new CustomeException(ErrorCode.MAIL_SEND_FAILED);
        }
    }
}