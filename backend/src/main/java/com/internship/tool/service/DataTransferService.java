package com.internship.tool.service;

import com.internship.tool.config.AiServiceClient;
import com.internship.tool.entity.DataTransfer;
import com.internship.tool.entity.User;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.DataTransferRepository;
import com.internship.tool.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.*;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataTransferService {

    private final DataTransferRepository repository;
    private final UserRepository userRepository;
    private final AiServiceClient aiClient;
    private final AuditService auditService;
    @Autowired
    private ObjectMapper objectMapper;

    @Cacheable(value = "transfers", key = "#pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<DataTransfer> getAll(Pageable pageable) {
        return repository.findByDeletedFalse(pageable);
    }

    @Cacheable(value = "transfer", key = "#id")
    public DataTransfer getById(Long id) {
        return repository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Transfer not found: " + id));
    }

    public Page<DataTransfer> search(String query, Pageable pageable) {
        return repository.search(query, pageable);
    }

    public Page<DataTransfer> filter(String status, String sourceCountry, String destinationCountry, Pageable pageable) {
        return repository.filter(status, sourceCountry, destinationCountry, pageable);
    }

    @Transactional
    @CacheEvict(value = "transfers", allEntries = true)
    public DataTransfer create(DataTransfer transfer, String username) {
        validateTransfer(transfer);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
        transfer.setCreatedBy(user);
        transfer.setDeleted(false);

        DataTransfer saved = repository.save(transfer);
        auditService.log("DataTransfer", saved.getId(), "CREATE", username,
                "Created transfer: " + saved.getTitle());

        // Async AI enrichment
        enrichWithAiAsync(saved.getId(), saved.getTitle(), saved.getSourceCountry(),
                saved.getDestinationCountry(), saved.getDataCategory(), saved.getTransferMechanism());

        return saved;
    }

    @Transactional
    @CacheEvict(value = {"transfers", "transfer"}, allEntries = true)
    public DataTransfer update(Long id, DataTransfer updated, String username) {
        DataTransfer existing = getById(id);
        validateTransfer(updated);

        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setSourceCountry(updated.getSourceCountry());
        existing.setDestinationCountry(updated.getDestinationCountry());
        existing.setDataCategory(updated.getDataCategory());
        existing.setTransferMechanism(updated.getTransferMechanism());
        existing.setStatus(updated.getStatus());
        existing.setRiskLevel(updated.getRiskLevel());
        existing.setComplianceScore(updated.getComplianceScore());
        existing.setDeadlineDate(updated.getDeadlineDate());

        DataTransfer saved = repository.save(existing);
        auditService.log("DataTransfer", id, "UPDATE", username, "Updated transfer: " + saved.getTitle());
        return saved;
    }

    @Transactional
    @CacheEvict(value = {"transfers", "transfer"}, allEntries = true)
    public void delete(Long id, String username) {
        DataTransfer transfer = getById(id);
        transfer.setDeleted(true);
        repository.save(transfer);
        auditService.log("DataTransfer", id, "DELETE", username, "Soft deleted transfer: " + transfer.getTitle());
    }

    public Map<String, Object> getStats() {
        long total = repository.countByDeletedFalse();
        long pending = repository.countByStatusAndDeletedFalse("PENDING");
        long approved = repository.countByStatusAndDeletedFalse("APPROVED");
        long rejected = repository.countByStatusAndDeletedFalse("REJECTED");
        Double avgScore = repository.averageComplianceScore();

        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("total", total);
        stats.put("pending", pending);
        stats.put("approved", approved);
        stats.put("rejected", rejected);
        stats.put("avgComplianceScore", avgScore != null ? Math.round(avgScore) : 0);
        return stats;
    }

    public List<DataTransfer> getUpcomingDeadlines() {
        return repository.findUpcomingDeadlines(LocalDate.now(), LocalDate.now().plusDays(7));
    }

    public List<DataTransfer> getAllForExport() {
        return repository.findByDeletedFalse();
    }

    @Async
    public void enrichWithAiAsync(Long id, String title, String sourceCountry,
                                   String destinationCountry, String dataCategory, String transferMechanism) {
        try {
            Map<String, Object> descResult = aiClient.describe(title, sourceCountry, destinationCountry, dataCategory, transferMechanism);
            if (descResult != null) {
                repository.findByIdAndDeletedFalse(id).ifPresent(t -> {
                    try {
                        t.setAiDescription(objectMapper.writeValueAsString(descResult));
                        // Extract compliance_score if present
                        if (descResult.containsKey("compliance_score")) {
                            Object score = descResult.get("compliance_score");
                            if (score instanceof Number) {
                                t.setComplianceScore(((Number) score).intValue());
                            }
                        }
                        if (descResult.containsKey("risk_level")) {
                            t.setRiskLevel((String) descResult.get("risk_level"));
                        }
                        repository.save(t);
                        log.info("AI description saved for transfer {}", id);
                    } catch (Exception e) {
                        log.error("Failed to save AI description for transfer {}: {}", id, e.getMessage());
                    }
                });
            }
        } catch (Exception e) {
            log.error("Async AI enrichment failed for transfer {}: {}", id, e.getMessage());
        }
    }

    private void validateTransfer(DataTransfer t) {
        if (t.getTitle() == null || t.getTitle().isBlank()) {
            throw new ValidationException("Title is required");
        }
        if (t.getSourceCountry() == null || t.getSourceCountry().isBlank()) {
            throw new ValidationException("Source country is required");
        }
        if (t.getDestinationCountry() == null || t.getDestinationCountry().isBlank()) {
            throw new ValidationException("Destination country is required");
        }
        if (t.getDataCategory() == null || t.getDataCategory().isBlank()) {
            throw new ValidationException("Data category is required");
        }
        if (t.getTransferMechanism() == null || t.getTransferMechanism().isBlank()) {
            throw new ValidationException("Transfer mechanism is required");
        }
    }
}
