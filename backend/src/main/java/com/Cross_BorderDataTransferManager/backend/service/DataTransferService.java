package com.Cross_BorderDataTransferManager.backend.service;

import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.repository.DataTransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DataTransferService {

    private final DataTransferRepository repository;

    @Cacheable(value = "dataTransfers", key = "#pageable.pageNumber + '_' + #pageable.pageSize")
    public Page<DataTransfer> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Cacheable(value = "dataTransfer", key = "#id")
    public Optional<DataTransfer> findById(Long id) {
        return repository.findById(id);
    }

    @CacheEvict(value = {"dataTransfers", "dataTransfer"}, allEntries = true)
    public DataTransfer save(DataTransfer dataTransfer) {
        return repository.save(dataTransfer);
    }

    @CacheEvict(value = {"dataTransfers", "dataTransfer"}, allEntries = true)
    public void deleteById(Long id) {
        repository.deleteById(id);
    }

    public List<DataTransfer> findByStatus(String status) {
        return repository.findByStatus(status);
    }

    public List<DataTransfer> search(String query) {
        return repository.findByCountry(query);
    }

    public Page<DataTransfer> searchWithFilters(String query, String status, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return repository.searchWithFilters(query, status, start, end, pageable);
    }

    public Map<String, Object> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long total = repository.count();
        long approved = repository.findByStatus("Approved").size();
        long pending = repository.findByStatus("Pending").size();
        stats.put("total", total);
        stats.put("approved", approved);
        stats.put("pending", pending);
        stats.put("others", total - approved - pending);
        return stats;
    }

}