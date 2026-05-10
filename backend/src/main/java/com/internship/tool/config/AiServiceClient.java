package com.internship.tool.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@Slf4j
public class AiServiceClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AiServiceClient(RestTemplate restTemplate,
                           @Value("${ai-service.url}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public Map<String, Object> describe(String title, String sourceCountry,
                                        String destinationCountry, String dataCategory,
                                        String transferMechanism) {
        try {
            Map<String, String> body = Map.of(
                "title", title,
                "source_country", sourceCountry,
                "destination_country", destinationCountry,
                "data_category", dataCategory,
                "transfer_mechanism", transferMechanism
            );
            return post("/describe", body);
        } catch (Exception e) {
            log.error("AI /describe call failed: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> recommend(String title, String sourceCountry,
                                          String destinationCountry, String dataCategory,
                                          String transferMechanism, Integer complianceScore) {
        try {
            Map<String, Object> body = Map.of(
                "title", title,
                "source_country", sourceCountry,
                "destination_country", destinationCountry,
                "data_category", dataCategory,
                "transfer_mechanism", transferMechanism,
                "compliance_score", complianceScore != null ? complianceScore : 0
            );
            return post("/recommend", body);
        } catch (Exception e) {
            log.error("AI /recommend call failed: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> generateReport(Long transferId, String title,
                                               String sourceCountry, String destinationCountry,
                                               String dataCategory, String transferMechanism,
                                               String status, Integer complianceScore) {
        try {
            Map<String, Object> body = Map.of(
                "transfer_id", transferId,
                "title", title,
                "source_country", sourceCountry,
                "destination_country", destinationCountry,
                "data_category", dataCategory,
                "transfer_mechanism", transferMechanism,
                "status", status,
                "compliance_score", complianceScore != null ? complianceScore : 0
            );
            return post("/generate-report", body);
        } catch (Exception e) {
            log.error("AI /generate-report call failed: {}", e.getMessage());
            return null;
        }
    }

    public Map<String, Object> health() {
        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(baseUrl + "/health", Map.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("AI /health call failed: {}", e.getMessage());
            return Map.of("status", "unavailable", "error", e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> post(String path, Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
            baseUrl + path, HttpMethod.POST, entity, Map.class
        );
        return response.getBody();
    }
}
