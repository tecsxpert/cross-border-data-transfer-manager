package com.Cross_BorderDataTransferManager.backend.aspect;

import com.Cross_BorderDataTransferManager.backend.entity.AuditLog;
import com.Cross_BorderDataTransferManager.backend.repository.AuditLogRepository;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@Aspect
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;

    @AfterReturning(pointcut = "execution(* com.Cross_BorderDataTransferManager.backend.service.DataTransferService.save(..))", returning = "result")
    public void logSave(JoinPoint joinPoint, Object result) {
        try {
            Object[] args = joinPoint.getArgs();
            String action = "UPDATE";
            if (args.length > 0 && args[0] instanceof com.Cross_BorderDataTransferManager.backend.entity.DataTransfer savedTransfer) {
                action = savedTransfer.getId() == null ? "CREATE" : "UPDATE";
            }
            if (result instanceof com.Cross_BorderDataTransferManager.backend.entity.DataTransfer transfer) {
                createLog(action, "DataTransfer", transfer.getId());
            }
        } catch (Exception ignored) {
        }
    }

    @AfterReturning(pointcut = "execution(* com.Cross_BorderDataTransferManager.backend.service.DataTransferService.deleteById(..))")
    public void logDelete(JoinPoint joinPoint) {
        try {
            Object[] args = joinPoint.getArgs();
            if (args.length > 0 && args[0] instanceof Long id) {
                createLog("DELETE", "DataTransfer", id);
            }
        } catch (Exception ignored) {
        }
    }

    private void createLog(String action, String entityType, Long entityId) {
        AuditLog log = new AuditLog();
        log.setAction(action);
        log.setEntityType(entityType);
        log.setEntityId(entityId);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            log.setUsername(auth.getName());
        }
        log.setTimestamp(java.time.LocalDateTime.now());
        auditLogRepository.save(log);
    }
}
