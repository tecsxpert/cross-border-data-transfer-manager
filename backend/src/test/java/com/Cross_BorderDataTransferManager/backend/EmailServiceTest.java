package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.service.EmailService;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.thymeleaf.TemplateEngine;

import java.util.Map;
import java.util.Properties;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Mock
    private TemplateEngine templateEngine;

    @InjectMocks
    private EmailService service;

    @Test
    void sendEmail_shouldRenderTemplateAndSendMimeMessage() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(eq("audit-notification"), any())).thenReturn("<p>Audit</p>");

        service.sendEmail("demo@example.com", "Subject", "audit-notification", Map.of("action", "CREATE"));

        verify(mailSender).send(message);
    }

    @Test
    void notificationHelpers_shouldUseExpectedTemplates() throws Exception {
        MimeMessage message = new MimeMessage(Session.getInstance(new Properties()));
        when(mailSender.createMimeMessage()).thenReturn(message);
        when(templateEngine.process(org.mockito.ArgumentMatchers.<String>any(), any())).thenReturn("<p>Notification</p>");

        service.sendAuditNotification("demo@example.com", "CREATE", "DataTransfer", 1L);
        service.sendTransferStatusChangeNotification("demo@example.com", 1L, "Approved");
        service.sendComplianceAlertNotification("demo@example.com", 1L, "High", 45);

        verify(templateEngine).process(eq("audit-notification"), any());
        verify(templateEngine).process(eq("status-change-notification"), any());
        verify(templateEngine).process(eq("compliance-alert"), any());
    }
}
