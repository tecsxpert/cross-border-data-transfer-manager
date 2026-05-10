package com.internship.tool.controller;

import com.internship.tool.config.AiServiceClient;
import com.internship.tool.entity.DataTransfer;
import com.internship.tool.service.DataTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Tag(name = "AI Features", description = "AI-powered analysis endpoints")
@SecurityRequirement(name = "bearerAuth")
public class AiController {

    private final AiServiceClient aiClient;
    private final DataTransferService dataTransferService;

    @PostMapping("/describe/{id}")
    @Operation(summary = "AI description for a transfer")
    public ResponseEntity<Map<String, Object>> describe(@PathVariable Long id) {
        DataTransfer t = dataTransferService.getById(id);
        Map<String, Object> result = aiClient.describe(
                t.getTitle(), t.getSourceCountry(), t.getDestinationCountry(),
                t.getDataCategory(), t.getTransferMechanism()
        );
        if (result == null) {
            return ResponseEntity.status(503).body(Map.of("error", "AI service unavailable", "is_fallback", true));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/recommend/{id}")
    @Operation(summary = "AI recommendations for a transfer")
    public ResponseEntity<Map<String, Object>> recommend(@PathVariable Long id) {
        DataTransfer t = dataTransferService.getById(id);
        Map<String, Object> result = aiClient.recommend(
                t.getTitle(), t.getSourceCountry(), t.getDestinationCountry(),
                t.getDataCategory(), t.getTransferMechanism(), t.getComplianceScore()
        );
        if (result == null) {
            return ResponseEntity.status(503).body(Map.of("error", "AI service unavailable", "is_fallback", true));
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping("/generate-report/{id}")
    @Operation(summary = "AI full compliance report for a transfer")
    public ResponseEntity<Map<String, Object>> generateReport(@PathVariable Long id) {
        DataTransfer t = dataTransferService.getById(id);
        Map<String, Object> result = aiClient.generateReport(
                t.getId(), t.getTitle(), t.getSourceCountry(), t.getDestinationCountry(),
                t.getDataCategory(), t.getTransferMechanism(), t.getStatus(), t.getComplianceScore()
        );
        if (result == null) {
            return ResponseEntity.status(503).body(Map.of("error", "AI service unavailable", "is_fallback", true));
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/health")
    @Operation(summary = "AI service health check")
    public ResponseEntity<Map<String, Object>> health() {
        return ResponseEntity.ok(aiClient.health());
    }
}
