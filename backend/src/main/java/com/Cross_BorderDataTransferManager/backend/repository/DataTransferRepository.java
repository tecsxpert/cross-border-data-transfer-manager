package com.Cross_BorderDataTransferManager.backend.repository;

import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;

public interface DataTransferRepository extends JpaRepository<DataTransfer, Long> {

    List<DataTransfer> findByStatus(String status);

    @Query("SELECT d FROM DataTransfer d WHERE LOWER(d.sourceCountry) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(d.destinationCountry) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<DataTransfer> findByCountry(@Param("query") String query);

    @Query("SELECT d FROM DataTransfer d WHERE (:q IS NULL OR LOWER(d.sourceCountry) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(d.destinationCountry) LIKE LOWER(CONCAT('%', :q, '%'))) AND (:status IS NULL OR d.status = :status) AND (:start IS NULL OR d.createdDate >= :start) AND (:end IS NULL OR d.createdDate <= :end)")
    Page<DataTransfer> searchWithFilters(@Param("q") String q,
                                         @Param("status") String status,
                                         @Param("start") LocalDateTime start,
                                         @Param("end") LocalDateTime end,
                                         Pageable pageable);

}