package com.Cross_BorderDataTransferManager.backend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    public void sendEmail(String to, String subject, String template, Map<String, Object> variables) throws MessagingException {
        Context context = new Context();
        context.setVariables(variables);
        String html = templateEngine.process(template, context);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(message);
    }

    @Scheduled(cron = "0 0 9 * * ?") // Daily at 9 AM
    public void sendDailyReminder() {
        // Logic to send reminders
        System.out.println("Sending daily reminder");
    }

    public void sendAuditNotification(String email, String action, String entityType, Long entityId) throws MessagingException {
        Map<String, Object> variables = Map.of(
            "action", action,
            "entityType", entityType,
            "entityId", entityId,
            "timestamp", java.time.LocalDateTime.now()
        );
        sendEmail(email, "Audit Notification: " + action, "audit-notification", variables);
    }

    public void sendTransferStatusChangeNotification(String email, Long transferId, String newStatus) throws MessagingException {
        Map<String, Object> variables = Map.of(
            "transferId", transferId,
            "newStatus", newStatus,
            "timestamp", java.time.LocalDateTime.now()
        );
        sendEmail(email, "Data Transfer Status Changed - ID: " + transferId, "status-change-notification", variables);
    }

    public void sendComplianceAlertNotification(String email, Long transferId, String riskLevel, Integer complianceScore) throws MessagingException {
        Map<String, Object> variables = Map.of(
            "transferId", transferId,
            "riskLevel", riskLevel,
            "complianceScore", complianceScore,
            "timestamp", java.time.LocalDateTime.now()
        );
        sendEmail(email, "Compliance Alert - Data Transfer ID: " + transferId, "compliance-alert", variables);
    }
}