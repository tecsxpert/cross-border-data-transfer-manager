package com.Cross_BorderDataTransferManager.backend.entity;

import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDateTime;

@Entity
@Table(name = "data_transfer")
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class DataTransfer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String sourceCountry;

    @Column(nullable = false)
    private String destinationCountry;

    @Column(nullable = false)
    private String dataType;

    @Column(nullable = false)
    private String status;

    private String description;

    private Integer complianceScore;

    private String riskLevel;

    private String legalBasis;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

}