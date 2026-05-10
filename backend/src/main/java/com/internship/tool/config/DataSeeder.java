package com.internship.tool.config;

import com.internship.tool.entity.DataTransfer;
import com.internship.tool.entity.User;
import com.internship.tool.repository.DataTransferRepository;
import com.internship.tool.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final DataTransferRepository transferRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        seedUsers();
        seedTransfers();
    }

    private void seedUsers() {
        if (userRepository.existsByUsername("admin")) return;

        userRepository.save(User.builder()
                .username("admin").email("admin@tool135.com")
                .password(passwordEncoder.encode("Admin@123")).role("ADMIN").active(true).build());

        userRepository.save(User.builder()
                .username("analyst").email("analyst@tool135.com")
                .password(passwordEncoder.encode("Analyst@123")).role("USER").active(true).build());

        log.info("Seeded default users");
    }

    private void seedTransfers() {
        if (transferRepository.countByDeletedFalse() >= 30) return;

        User admin = userRepository.findByUsername("admin").orElseThrow();

        List<DataTransfer> records = List.of(
            build("GDPR Compliance Data Export", "European Union", "United States", "Personal Data", "Standard Contractual Clauses", "APPROVED", "LOW", 88, LocalDate.now().plusDays(30), admin),
            build("Healthcare Records Transfer", "India", "United Kingdom", "Medical Data", "Binding Corporate Rules", "PENDING", "HIGH", 45, LocalDate.now().plusDays(7), admin),
            build("Financial Transaction Logs", "Singapore", "Australia", "Financial Data", "Adequacy Decision", "APPROVED", "MEDIUM", 72, LocalDate.now().plusDays(60), admin),
            build("Employee HR Data Sync", "Germany", "Canada", "HR Data", "Standard Contractual Clauses", "APPROVED", "LOW", 91, LocalDate.now().plusDays(45), admin),
            build("Customer Analytics Transfer", "United States", "Brazil", "Analytics Data", "Consent Based", "PENDING", "MEDIUM", 58, LocalDate.now().plusDays(14), admin),
            build("Biometric Data Export", "Japan", "South Korea", "Biometric Data", "Binding Corporate Rules", "REJECTED", "CRITICAL", 22, LocalDate.now().plusDays(3), admin),
            build("Supply Chain Data Sync", "China", "Mexico", "Operational Data", "Standard Contractual Clauses", "PENDING", "HIGH", 49, LocalDate.now().plusDays(21), admin),
            build("Payment Processing Records", "France", "United States", "Financial Data", "Adequacy Decision", "APPROVED", "LOW", 85, LocalDate.now().plusDays(90), admin),
            build("Research Dataset Transfer", "United Kingdom", "India", "Research Data", "Consent Based", "APPROVED", "MEDIUM", 67, LocalDate.now().plusDays(30), admin),
            build("IoT Device Telemetry", "Netherlands", "Singapore", "IoT Data", "Standard Contractual Clauses", "PENDING", "LOW", 74, LocalDate.now().plusDays(15), admin),
            build("Social Media Analytics", "United States", "European Union", "Personal Data", "Adequacy Decision", "APPROVED", "MEDIUM", 79, LocalDate.now().plusDays(45), admin),
            build("Legal Document Archive", "Australia", "United Kingdom", "Legal Data", "Binding Corporate Rules", "APPROVED", "LOW", 93, LocalDate.now().plusDays(120), admin),
            build("Tax Records Export", "Canada", "United States", "Financial Data", "Adequacy Decision", "APPROVED", "LOW", 88, LocalDate.now().plusDays(60), admin),
            build("Clinical Trial Data", "Germany", "Japan", "Medical Data", "Standard Contractual Clauses", "PENDING", "HIGH", 41, LocalDate.now().plusDays(10), admin),
            build("E-commerce Order History", "South Korea", "United States", "Commercial Data", "Consent Based", "APPROVED", "LOW", 82, LocalDate.now().plusDays(30), admin),
            build("Cybersecurity Logs Transfer", "United States", "Israel", "Security Data", "Standard Contractual Clauses", "PENDING", "HIGH", 55, LocalDate.now().plusDays(5), admin),
            build("Marketing Campaign Data", "Brazil", "Portugal", "Marketing Data", "Adequacy Decision", "APPROVED", "LOW", 76, LocalDate.now().plusDays(25), admin),
            build("Insurance Claims Records", "United Kingdom", "Ireland", "Financial Data", "Adequacy Decision", "APPROVED", "LOW", 89, LocalDate.now().plusDays(40), admin),
            build("Education Transcript Export", "India", "United States", "Academic Data", "Consent Based", "PENDING", "MEDIUM", 63, LocalDate.now().plusDays(20), admin),
            build("Genomic Research Data", "United States", "European Union", "Genetic Data", "Binding Corporate Rules", "REJECTED", "CRITICAL", 18, LocalDate.now().plusDays(2), admin),
            build("Logistics Tracking Data", "China", "Germany", "Operational Data", "Standard Contractual Clauses", "APPROVED", "MEDIUM", 71, LocalDate.now().plusDays(35), admin),
            build("Customer Support Logs", "Philippines", "United States", "Personal Data", "Consent Based", "PENDING", "MEDIUM", 52, LocalDate.now().plusDays(12), admin),
            build("Banking KYC Documents", "UAE", "United Kingdom", "Identity Data", "Binding Corporate Rules", "PENDING", "HIGH", 44, LocalDate.now().plusDays(8), admin),
            build("Telecom Call Records", "India", "Singapore", "Communications Data", "Standard Contractual Clauses", "APPROVED", "MEDIUM", 68, LocalDate.now().plusDays(50), admin),
            build("Smart City Data Export", "South Korea", "Netherlands", "IoT Data", "Adequacy Decision", "APPROVED", "LOW", 84, LocalDate.now().plusDays(70), admin),
            build("Pharmaceutical Records", "Switzerland", "United States", "Medical Data", "Standard Contractual Clauses", "APPROVED", "MEDIUM", 77, LocalDate.now().plusDays(55), admin),
            build("Energy Grid Analytics", "Germany", "France", "Operational Data", "Adequacy Decision", "APPROVED", "LOW", 90, LocalDate.now().plusDays(80), admin),
            build("Autonomous Vehicle Data", "United States", "Japan", "IoT Data", "Binding Corporate Rules", "PENDING", "HIGH", 47, LocalDate.now().plusDays(6), admin),
            build("Cross-Border Payments", "Singapore", "India", "Financial Data", "Standard Contractual Clauses", "APPROVED", "MEDIUM", 73, LocalDate.now().plusDays(28), admin),
            build("Satellite Imagery Transfer", "United States", "European Union", "Geospatial Data", "Standard Contractual Clauses", "APPROVED", "LOW", 86, LocalDate.now().plusDays(100), admin)
        );

        transferRepository.saveAll(records);
        log.info("Seeded {} demo records", records.size());
    }

    private DataTransfer build(String title, String src, String dst, String category,
                                String mechanism, String status, String risk,
                                int score, LocalDate deadline, User user) {
        return DataTransfer.builder()
                .title(title).sourceCountry(src).destinationCountry(dst)
                .dataCategory(category).transferMechanism(mechanism)
                .status(status).riskLevel(risk).complianceScore(score)
                .deadlineDate(deadline).createdBy(user).deleted(false).build();
    }
}
