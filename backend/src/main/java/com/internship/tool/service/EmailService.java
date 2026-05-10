package com.internship.tool.service;

import com.internship.tool.entity.DataTransfer;
import com.internship.tool.repository.DataTransferRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.mail.internet.MimeMessage;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final DataTransferRepository transferRepository;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Scheduled(cron = "0 0 8 * * MON-FRI")
    public void sendDeadlineAlerts() {
        List<DataTransfer> upcoming = transferRepository
                .findUpcomingDeadlines(LocalDate.now(), LocalDate.now().plusDays(7));

        for (DataTransfer t : upcoming) {
            if (t.getCreatedBy() != null && t.getCreatedBy().getEmail() != null) {
                try {
                    sendDeadlineEmail(t.getCreatedBy().getEmail(), t);
                } catch (Exception e) {
                    log.warn("Failed to send deadline alert for transfer {}: {}", t.getId(), e.getMessage());
                }
            }
        }
        log.info("Deadline alerts sent for {} transfers", upcoming.size());
    }

    public void sendDeadlineEmail(String toEmail, DataTransfer transfer) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom(fromEmail);
            helper.setTo(toEmail);
            helper.setSubject("[Tool-135] Deadline Alert: " + transfer.getTitle());
            helper.setText(buildEmailBody(transfer), true);
            mailSender.send(message);
            log.info("Deadline alert sent to {} for transfer {}", toEmail, transfer.getId());
        } catch (Exception e) {
            log.error("Email send failed: {}", e.getMessage());
        }
    }

    private String buildEmailBody(DataTransfer t) {
        return String.format("""
            <html><body style="font-family: Arial, sans-serif; color: #333;">
            <div style="max-width:600px;margin:auto;padding:24px;border:1px solid #e0e0e0;border-radius:8px;">
              <h2 style="color:#1B4F8A;">⚠️ Transfer Deadline Alert</h2>
              <p>The following cross-border data transfer has a deadline within 7 days:</p>
              <table style="width:100%%;border-collapse:collapse;">
                <tr><td style="padding:8px;font-weight:bold;">Title</td><td>%s</td></tr>
                <tr style="background:#f5f5f5;"><td style="padding:8px;font-weight:bold;">Source</td><td>%s</td></tr>
                <tr><td style="padding:8px;font-weight:bold;">Destination</td><td>%s</td></tr>
                <tr style="background:#f5f5f5;"><td style="padding:8px;font-weight:bold;">Status</td><td>%s</td></tr>
                <tr><td style="padding:8px;font-weight:bold;">Deadline</td><td><strong style="color:#e53e3e;">%s</strong></td></tr>
                <tr style="background:#f5f5f5;"><td style="padding:8px;font-weight:bold;">Compliance Score</td><td>%s%%</td></tr>
              </table>
              <p style="margin-top:16px;color:#666;">Please review and take action in the <a href="http://localhost">Tool-135 Dashboard</a>.</p>
            </div>
            </body></html>
            """,
            t.getTitle(), t.getSourceCountry(), t.getDestinationCountry(),
            t.getStatus(), t.getDeadlineDate(), t.getComplianceScore() != null ? t.getComplianceScore() : "N/A"
        );
    }
}
