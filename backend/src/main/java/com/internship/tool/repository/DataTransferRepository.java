package com.internship.tool.repository;

import com.internship.tool.entity.DataTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface DataTransferRepository extends JpaRepository<DataTransfer, Long> {

    Page<DataTransfer> findByDeletedFalse(Pageable pageable);

    Optional<DataTransfer> findByIdAndDeletedFalse(Long id);

    @Query("SELECT d FROM DataTransfer d WHERE d.deleted = false AND " +
           "(LOWER(d.title) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           " LOWER(d.sourceCountry) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           " LOWER(d.destinationCountry) LIKE LOWER(CONCAT('%',:q,'%')) OR " +
           " LOWER(d.dataCategory) LIKE LOWER(CONCAT('%',:q,'%')))")
    Page<DataTransfer> search(@Param("q") String query, Pageable pageable);

    @Query("SELECT d FROM DataTransfer d WHERE d.deleted = false AND " +
           "(:status IS NULL OR d.status = :status) AND " +
           "(:sourceCountry IS NULL OR LOWER(d.sourceCountry) LIKE LOWER(CONCAT('%',:sourceCountry,'%'))) AND " +
           "(:destinationCountry IS NULL OR LOWER(d.destinationCountry) LIKE LOWER(CONCAT('%',:destinationCountry,'%')))")
    Page<DataTransfer> filter(@Param("status") String status,
                               @Param("sourceCountry") String sourceCountry,
                               @Param("destinationCountry") String destinationCountry,
                               Pageable pageable);

    long countByDeletedFalse();

    long countByStatusAndDeletedFalse(String status);

    @Query("SELECT AVG(d.complianceScore) FROM DataTransfer d WHERE d.deleted = false AND d.complianceScore IS NOT NULL")
    Double averageComplianceScore();

    @Query("SELECT d FROM DataTransfer d WHERE d.deleted = false AND " +
           "d.deadlineDate IS NOT NULL AND d.deadlineDate BETWEEN :from AND :to")
    List<DataTransfer> findUpcomingDeadlines(@Param("from") LocalDate from, @Param("to") LocalDate to);

    List<DataTransfer> findByDeletedFalse();
}
