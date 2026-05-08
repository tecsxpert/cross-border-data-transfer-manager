package com.Cross_BorderDataTransferManager.backend.config;

import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.entity.User;
import com.Cross_BorderDataTransferManager.backend.repository.DataTransferRepository;
import com.Cross_BorderDataTransferManager.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataLoader implements CommandLineRunner {

    private final DataTransferRepository dataTransferRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // Seed users
        if (userRepository.count() == 0) {
            User user1 = new User();
            user1.setUsername("user1");
            user1.setPassword(passwordEncoder.encode("password123"));
            user1.setEmail("user1@example.com");
            user1.setRole("USER");
            userRepository.save(user1);

            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@example.com");
            admin.setRole("ADMIN");
            userRepository.save(admin);
        }

        // Seed data transfers
        if (dataTransferRepository.count() == 0) {
            String[] countries = {"USA", "UK", "Germany", "France", "Canada", "Australia", "Japan", "India", "Brazil", "South Africa"};
            String[] dataTypes = {"Personal Data", "Financial Data", "Health Data", "Marketing Data"};
            String[] statuses = {"Pending", "Approved", "Rejected", "Under Review"};
            String[] riskLevels = {"Low", "Medium", "High"};
            String[] legalBases = {"Consent", "Legitimate Interest", "Contract", "Legal Obligation"};

            for (int i = 1; i <= 30; i++) {
                DataTransfer dt = new DataTransfer();
                dt.setSourceCountry(countries[i % countries.length]);
                dt.setDestinationCountry(countries[(i + 1) % countries.length]);
                dt.setDataType(dataTypes[i % dataTypes.length]);
                dt.setStatus(statuses[i % statuses.length]);
                dt.setDescription("Demo transfer " + i + " for compliance assessment.");
                dt.setComplianceScore(60 + (i * 3) % 40); // 60-99
                dt.setRiskLevel(riskLevels[i % riskLevels.length]);
                dt.setLegalBasis(legalBases[i % legalBases.length]);
                dataTransferRepository.save(dt);
            }
        }
    }

}
