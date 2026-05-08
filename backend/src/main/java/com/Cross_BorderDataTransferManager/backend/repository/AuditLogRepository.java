package com.Cross_BorderDataTransferManager.backend.repository;

import com.Cross_BorderDataTransferManager.backend.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
}