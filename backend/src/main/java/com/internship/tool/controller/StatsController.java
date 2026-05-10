package com.internship.tool.controller;

import com.internship.tool.repository.AuditLogRepository;
import com.internship.tool.service.DataTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Tag(name = "Statistics", description = "Dashboard KPI and audit endpoints")
@SecurityRequirement(name = "bearerAuth")
public class StatsController {

    private final DataTransferService dataTransferService;
    private final AuditLogRepository auditLogRepository;

    @GetMapping("/stats")
    @Operation(summary = "Dashboard KPI statistics")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(dataTransferService.getStats());
    }

    @GetMapping("/audit-log")
    @Operation(summary = "Paginated audit log")
    public ResponseEntity<?> getAuditLog(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(auditLogRepository.findAllByOrderByCreatedAtDesc(pageable));
    }
}
