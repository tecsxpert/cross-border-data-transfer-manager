package com.Cross_BorderDataTransferManager.backend.controller;

import com.Cross_BorderDataTransferManager.backend.dto.PageResponse;
import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.service.DataTransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/data-transfers")
@RequiredArgsConstructor
@Tag(name = "Data Transfer Management", description = "APIs for managing cross-border data transfers")
public class DataTransferController {

    private final DataTransferService service;

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get all data transfers", description = "Retrieve paginated list of data transfers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved data transfers"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public PageResponse<DataTransfer> getAll(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {
        Page<DataTransfer> transfers = service.findAll(PageRequest.of(page, size));
        return new PageResponse<>(
                transfers.getContent(),
                transfers.getNumber(),
                transfers.getSize(),
                transfers.getTotalElements(),
                transfers.getTotalPages(),
                transfers.isLast()
        );
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get data transfer by ID", description = "Retrieve a specific data transfer by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved data transfer"),
        @ApiResponse(responseCode = "404", description = "Data transfer not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<DataTransfer> getById(@Parameter(description = "Data transfer ID") @PathVariable Long id) {
        return service.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create new data transfer", description = "Create a new data transfer record")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Data transfer created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<DataTransfer> create(@RequestBody DataTransfer dataTransfer) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.save(dataTransfer));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update data transfer", description = "Update an existing data transfer by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Data transfer updated successfully"),
        @ApiResponse(responseCode = "404", description = "Data transfer not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<DataTransfer> update(
            @Parameter(description = "Data transfer ID") @PathVariable Long id,
            @RequestBody DataTransfer dataTransfer) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        dataTransfer.setId(id);
        return ResponseEntity.ok(service.save(dataTransfer));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Delete data transfer", description = "Delete a data transfer by ID (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Data transfer deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Data transfer not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<Void> delete(@Parameter(description = "Data transfer ID") @PathVariable Long id) {
        if (!service.findById(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        service.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Search data transfers", description = "Search data transfers by country name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public List<DataTransfer> search(@Parameter(description = "Search query (country name)") @RequestParam String q) {
        return service.search(q);
    }

    @GetMapping("/search/filtered")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Search and filter data transfers", description = "Search data transfers by country, status and date range")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Filtered search completed successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public PageResponse<DataTransfer> searchFiltered(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        LocalDateTime start = parseDate(startDate);
        LocalDateTime end = parseDate(endDate);
        Page<DataTransfer> transfers = service.searchWithFilters(q, status, start, end, PageRequest.of(page, size));
        return new PageResponse<>(
                transfers.getContent(),
                transfers.getNumber(),
                transfers.getSize(),
                transfers.getTotalElements(),
                transfers.getTotalPages(),
                transfers.isLast()
        );
    }

    @PostMapping("/upload")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Upload a file", description = "Upload a supported file for a data transfer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "File uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be 5MB or less");
        }
        String contentType = file.getContentType();
        if (contentType == null || !(contentType.equals("text/csv") || contentType.equals("application/pdf") || contentType.equals("image/png") || contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("File type not supported. Allowed types: CSV, PDF, PNG, JPEG");
        }
        Path uploadDir = Paths.get("uploads");
        Files.createDirectories(uploadDir);
        Path filePath = uploadDir.resolve(System.currentTimeMillis() + "-" + file.getOriginalFilename());
        file.transferTo(filePath);
        return ResponseEntity.ok("Uploaded file: " + filePath.getFileName());
    }

    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get data transfer statistics", description = "Retrieve statistics about data transfers")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public Map<String, Object> getStats() {
        return service.getStats();
    }

    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Export data transfers to CSV", description = "Download all data transfers as CSV file")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "CSV file generated successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    public ResponseEntity<String> exportCsv() {
        Page<DataTransfer> transfers = service.findAll(PageRequest.of(0, 1000));
        StringBuilder csv = new StringBuilder();
        csv.append("ID,Source Country,Destination Country,Data Type,Status,Description,Compliance Score,Risk Level,Legal Basis,Created Date\n");
        for (DataTransfer dt : transfers) {
            csv.append(String.format("%d,%s,%s,%s,%s,%s,%d,%s,%s,%s\n",
                    dt.getId(),
                    dt.getSourceCountry(),
                    dt.getDestinationCountry(),
                    dt.getDataType(),
                    dt.getStatus(),
                    dt.getDescription() != null ? dt.getDescription() : "",
                    dt.getComplianceScore() != null ? dt.getComplianceScore() : 0,
                    dt.getRiskLevel() != null ? dt.getRiskLevel() : "",
                    dt.getLegalBasis() != null ? dt.getLegalBasis() : "",
                    dt.getCreatedDate()));
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=data-transfers.csv")
                .header(HttpHeaders.CONTENT_TYPE, "text/csv")
                .body(csv.toString());
    }

    private LocalDateTime parseDate(String date) {
        if (date == null || date.isBlank()) {
            return null;
        }
        try {
            return LocalDateTime.parse(date);
        } catch (DateTimeParseException ex) {
            throw new IllegalArgumentException("Date format must be ISO-8601, e.g. 2026-05-03T00:00:00");
        }
    }

}
