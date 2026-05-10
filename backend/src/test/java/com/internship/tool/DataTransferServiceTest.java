package com.internship.tool;

import com.internship.tool.entity.DataTransfer;
import com.internship.tool.exception.ResourceNotFoundException;
import com.internship.tool.exception.ValidationException;
import com.internship.tool.repository.DataTransferRepository;
import com.internship.tool.repository.UserRepository;
import com.internship.tool.config.AiServiceClient;
import com.internship.tool.service.AuditService;
import com.internship.tool.service.DataTransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DataTransferServiceTest {

    @Mock DataTransferRepository repository;
    @Mock UserRepository userRepository;
    @Mock AiServiceClient aiClient;
    @Mock AuditService auditService;

    @InjectMocks DataTransferService service;

    private DataTransfer sampleTransfer;

    @BeforeEach
    void setUp() {
        sampleTransfer = DataTransfer.builder()
                .id(1L).title("Test Transfer")
                .sourceCountry("Germany").destinationCountry("USA")
                .dataCategory("Personal Data").transferMechanism("SCCs")
                .status("PENDING").complianceScore(75).deleted(false)
                .build();
    }

    @Test
    void getById_existingId_returnsTransfer() {
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleTransfer));
        DataTransfer result = service.getById(1L);
        assertThat(result.getTitle()).isEqualTo("Test Transfer");
    }

    @Test
    void getById_missingId_throwsNotFoundException() {
        when(repository.findByIdAndDeletedFalse(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("99");
    }

    @Test
    void create_validTransfer_savesAndReturns() {
        var user = com.internship.tool.entity.User.builder()
                .id(1L).username("admin").email("a@b.com")
                .password("pass").role("ADMIN").active(true).build();
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(repository.save(any())).thenReturn(sampleTransfer);
        doNothing().when(auditService).log(any(), any(), any(), any(), any());

        DataTransfer result = service.create(sampleTransfer, "admin");
        assertThat(result).isNotNull();
        verify(repository, times(1)).save(any());
    }

    @Test
    void create_missingTitle_throwsValidationException() {
        DataTransfer invalid = DataTransfer.builder()
                .sourceCountry("Germany").destinationCountry("USA")
                .dataCategory("Personal Data").transferMechanism("SCCs").build();
        assertThatThrownBy(() -> service.create(invalid, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Title");
    }

    @Test
    void create_missingSourceCountry_throwsValidationException() {
        DataTransfer invalid = DataTransfer.builder()
                .title("Test").destinationCountry("USA")
                .dataCategory("Personal Data").transferMechanism("SCCs").build();
        assertThatThrownBy(() -> service.create(invalid, "admin"))
                .isInstanceOf(ValidationException.class)
                .hasMessageContaining("Source country");
    }

    @Test
    void delete_existingTransfer_softDeletes() {
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleTransfer));
        when(repository.save(any())).thenReturn(sampleTransfer);
        doNothing().when(auditService).log(any(), any(), any(), any(), any());

        service.delete(1L, "admin");
        assertThat(sampleTransfer.getDeleted()).isTrue();
    }

    @Test
    void getStats_returnsCorrectCounts() {
        when(repository.countByDeletedFalse()).thenReturn(10L);
        when(repository.countByStatusAndDeletedFalse("PENDING")).thenReturn(3L);
        when(repository.countByStatusAndDeletedFalse("APPROVED")).thenReturn(6L);
        when(repository.countByStatusAndDeletedFalse("REJECTED")).thenReturn(1L);
        when(repository.averageComplianceScore()).thenReturn(75.5);

        Map<String, Object> stats = service.getStats();
        assertThat(stats.get("total")).isEqualTo(10L);
        assertThat(stats.get("pending")).isEqualTo(3L);
        assertThat(stats.get("approved")).isEqualTo(6L);
    }

    @Test
    void update_existingTransfer_updatesFields() {
        when(repository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(sampleTransfer));
        when(repository.save(any())).thenReturn(sampleTransfer);
        doNothing().when(auditService).log(any(), any(), any(), any(), any());

        DataTransfer updated = DataTransfer.builder()
                .title("Updated Title").sourceCountry("France").destinationCountry("Canada")
                .dataCategory("Financial Data").transferMechanism("BCRs")
                .status("APPROVED").build();

        DataTransfer result = service.update(1L, updated, "admin");
        assertThat(result).isNotNull();
        verify(repository).save(argThat(t -> t.getTitle().equals("Updated Title")));
    }

    @Test
    void getAll_returnsPagedResults() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<DataTransfer> page = new PageImpl<>(List.of(sampleTransfer));
        when(repository.findByDeletedFalse(pageable)).thenReturn(page);

        Page<DataTransfer> result = service.getAll(pageable);
        assertThat(result.getTotalElements()).isEqualTo(1);
    }

    @Test
    void delete_nonExistentTransfer_throwsNotFoundException() {
        when(repository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.delete(999L, "admin"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
