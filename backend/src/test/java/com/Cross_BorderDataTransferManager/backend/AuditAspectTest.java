package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.aspect.AuditAspect;
import com.Cross_BorderDataTransferManager.backend.entity.AuditLog;
import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.repository.AuditLogRepository;
import org.aspectj.lang.JoinPoint;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuditAspectTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @Mock
    private JoinPoint joinPoint;

    @InjectMocks
    private AuditAspect auditAspect;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void logSave_shouldCreateAuditLogForCreatedTransfer() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("auditor", null)
        );
        DataTransfer input = new DataTransfer();
        DataTransfer result = new DataTransfer();
        result.setId(10L);
        when(joinPoint.getArgs()).thenReturn(new Object[] { input });

        auditAspect.logSave(joinPoint, result);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo("CREATE");
        assertThat(captor.getValue().getEntityId()).isEqualTo(10L);
        assertThat(captor.getValue().getUsername()).isEqualTo("auditor");
    }

    @Test
    void logSave_shouldCreateAuditLogForUpdatedTransfer() {
        DataTransfer input = new DataTransfer();
        input.setId(9L);
        DataTransfer result = new DataTransfer();
        result.setId(9L);
        when(joinPoint.getArgs()).thenReturn(new Object[] { input });

        auditAspect.logSave(joinPoint, result);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo("UPDATE");
    }

    @Test
    void logDelete_shouldCreateDeleteAuditLog() {
        when(joinPoint.getArgs()).thenReturn(new Object[] { 7L });

        auditAspect.logDelete(joinPoint);

        ArgumentCaptor<AuditLog> captor = ArgumentCaptor.forClass(AuditLog.class);
        verify(auditLogRepository).save(captor.capture());
        assertThat(captor.getValue().getAction()).isEqualTo("DELETE");
        assertThat(captor.getValue().getEntityId()).isEqualTo(7L);
    }

    @Test
    void logDelete_shouldIgnoreNonLongArgument() {
        when(joinPoint.getArgs()).thenReturn(new Object[] { "bad-id" });

        auditAspect.logDelete(joinPoint);

        verifyNoInteractions(auditLogRepository);
    }
}
