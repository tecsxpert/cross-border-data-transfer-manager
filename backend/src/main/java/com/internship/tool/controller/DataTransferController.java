package com.internship.tool.controller;

import com.internship.tool.entity.DataTransfer;
import com.internship.tool.service.DataTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.csv.*;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
@Tag(name = "Data Transfers", description = "CRUD operations for cross-border data transfers")
@SecurityRequirement(name = "bearerAuth")
public class DataTransferController {

    private final DataTransferService service;

    @GetMapping
    @Operation(summary = "List all transfers (paginated)")
    public ResponseEntity<Page<DataTransfer>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        Sort sort = direction.equalsIgnoreCase("asc") ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return ResponseEntity.ok(service.getAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get transfer by ID")
    public ResponseEntity<DataTransfer> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search and filter transfers")
    public ResponseEntity<Page<DataTransfer>> search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String sourceCountry,
            @RequestParam(required = false) String destinationCountry,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        if (q != null && !q.isBlank()) {
            return ResponseEntity.ok(service.search(q, pageable));
        }
        return ResponseEntity.ok(service.filter(status, sourceCountry, destinationCountry, pageable));
    }

    @PostMapping
    @Operation(summary = "Create a new transfer")
    public ResponseEntity<DataTransfer> create(@Valid @RequestBody DataTransferRequest req, Authentication auth) {
        DataTransfer transfer = mapRequest(req);
        return ResponseEntity.status(201).body(service.create(transfer, auth.getName()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing transfer")
    public ResponseEntity<DataTransfer> update(@PathVariable Long id,
                                               @Valid @RequestBody DataTransferRequest req,
                                               Authentication auth) {
        DataTransfer transfer = mapRequest(req);
        return ResponseEntity.ok(service.update(id, transfer, auth.getName()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete a transfer")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
    public ResponseEntity<Map<String, String>> delete(@PathVariable Long id, Authentication auth) {
        service.delete(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Transfer deleted successfully"));
    }

    @GetMapping("/export")
    @Operation(summary = "Export all transfers as CSV")
    public void exportCsv(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transfers.csv");

        List<DataTransfer> transfers = service.getAllForExport();
        try (PrintWriter writer = response.getWriter();
             CSVPrinter csv = new CSVPrinter(writer, CSVFormat.DEFAULT.builder()
                     .setHeader("ID", "Title", "Source Country", "Destination Country",
                             "Data Category", "Transfer Mechanism", "Status",
                             "Risk Level", "Compliance Score", "Deadline", "Created At")
                     .build())) {
            for (DataTransfer t : transfers) {
                csv.printRecord(
                        t.getId(), t.getTitle(), t.getSourceCountry(), t.getDestinationCountry(),
                        t.getDataCategory(), t.getTransferMechanism(), t.getStatus(),
                        t.getRiskLevel(), t.getComplianceScore(), t.getDeadlineDate(), t.getCreatedAt()
                );
            }
        }
    }

    private DataTransfer mapRequest(DataTransferRequest req) {
        return DataTransfer.builder()
                .title(req.title)
                .description(req.description)
                .sourceCountry(req.sourceCountry)
                .destinationCountry(req.destinationCountry)
                .dataCategory(req.dataCategory)
                .transferMechanism(req.transferMechanism)
                .status(req.status != null ? req.status : "PENDING")
                .riskLevel(req.riskLevel)
                .complianceScore(req.complianceScore)
                .deadlineDate(req.deadlineDate != null ? LocalDate.parse(req.deadlineDate) : null)
                .build();
    }

    public record DataTransferRequest(
            @jakarta.validation.constraints.NotBlank(message = "Title is required") String title,
            String description,
            @jakarta.validation.constraints.NotBlank(message = "Source country is required") String sourceCountry,
            @jakarta.validation.constraints.NotBlank(message = "Destination country is required") String destinationCountry,
            @jakarta.validation.constraints.NotBlank(message = "Data category is required") String dataCategory,
            @jakarta.validation.constraints.NotBlank(message = "Transfer mechanism is required") String transferMechanism,
            String status,
            String riskLevel,
            @jakarta.validation.constraints.Min(0) @jakarta.validation.constraints.Max(100) Integer complianceScore,
            String deadlineDate
    ) {}
}
