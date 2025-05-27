package com.ptit.service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final RestTemplate restTemplate;
    private final TemplateEngine templateEngine;

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    @Value("${brevo.sender.email}")
    private String senderEmail;

    @Value("${brevo.sender.name}")
    private String senderName;

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";

    public void sendEmail(String to, String subject, String templateName, Context context) {
        // Render nội dung email từ template và context
        String htmlContent = templateEngine.process(templateName, context);

        // Cấu trúc nội dung gửi đến Brevo API
        Map<String, Object> emailRequest = new HashMap<>();
        emailRequest.put("sender", Map.of("email", senderEmail, "name", senderName));
        emailRequest.put("to", new Map[] { Map.of("email", to) });
        emailRequest.put("subject", subject);
        emailRequest.put("htmlContent", htmlContent);

        // Tạo headers cho request với API key
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("api-key", brevoApiKey);

        try {
            // Gửi POST request tới Brevo API
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailRequest, headers);
            ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, entity, String.class);
            log.info("Email sent successfully to {} with status {}", to, response.getStatusCode());
        } catch (Exception e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());
        }
    }

    public void sendEmails(List<Map<String, Object>> recipientsAndContexts, String subject, String templateName) {
        for (Map<String, Object> recipientData : recipientsAndContexts) {
            String email = (String) recipientData.get("email");
            Context context = (Context) recipientData.get("context");

            // Render nội dung email từ template và context
            String htmlContent = templateEngine.process(templateName, context);

            // Cấu trúc nội dung gửi đến API (Ví dụ: Brevo)
            Map<String, Object> emailRequest = new HashMap<>();
            emailRequest.put("sender", Map.of("email", senderEmail, "name", senderName));
            emailRequest.put("to", List.of(Map.of("email", email)));
            emailRequest.put("subject", subject);
            emailRequest.put("htmlContent", htmlContent);

            // Tạo headers cho request với API key
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("api-key", brevoApiKey);

            try {
                // Gửi POST request tới Brevo API
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(emailRequest, headers);
                ResponseEntity<String> response = restTemplate.postForEntity(BREVO_API_URL, entity, String.class);
                log.info("Email sent successfully to {} with status {}", email, response.getStatusCode());
            } catch (Exception e) {
                log.error("Failed to send email to {}: {}", email, e.getMessage());
            }
        }
    }
}
