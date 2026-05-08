package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.repository.DataTransferRepository;
import com.Cross_BorderDataTransferManager.backend.service.DataTransferService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTransferServiceTest {

    @Mock
    private DataTransferRepository repository;

    @InjectMocks
    private DataTransferService service;

    @Test
    void findAll_shouldDelegateToRepository() {
        PageRequest pageable = PageRequest.of(0, 10);
        when(repository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(new DataTransfer())));

        assertThat(service.findAll(pageable).getContent()).hasSize(1);
    }

    @Test
    void findById_shouldReturnRepositoryResult() {
        DataTransfer transfer = new DataTransfer();
        when(repository.findById(1L)).thenReturn(Optional.of(transfer));

        assertThat(service.findById(1L)).contains(transfer);
    }

    @Test
    void save_shouldPersistTransfer() {
        DataTransfer transfer = new DataTransfer();
        when(repository.save(transfer)).thenReturn(transfer);

        assertThat(service.save(transfer)).isSameAs(transfer);
    }

    @Test
    void deleteById_shouldDeleteTransfer() {
        service.deleteById(4L);

        verify(repository).deleteById(4L);
    }

    @Test
    void searchWithFilters_shouldDelegateToRepository() {
        PageRequest pageable = PageRequest.of(1, 5);
        LocalDateTime start = LocalDateTime.parse("2026-05-01T00:00:00");
        LocalDateTime end = LocalDateTime.parse("2026-05-08T23:59:59");
        when(repository.searchWithFilters("usa", "Approved", start, end, pageable))
                .thenReturn(new PageImpl<>(List.of(new DataTransfer())));

        assertThat(service.searchWithFilters("usa", "Approved", start, end, pageable).getContent()).hasSize(1);
    }

    @Test
    void getStats_shouldReturnTotalApprovedPendingAndOthers() {
        when(repository.count()).thenReturn(30L);
        when(repository.findByStatus("Approved")).thenReturn(List.of(new DataTransfer(), new DataTransfer()));
        when(repository.findByStatus("Pending")).thenReturn(List.of(new DataTransfer()));

        assertThat(service.getStats())
                .containsEntry("total", 30L)
                .containsEntry("approved", 2L)
                .containsEntry("pending", 1L)
                .containsEntry("others", 27L);
    }
}
