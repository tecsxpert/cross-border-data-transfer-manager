package com.Cross_BorderDataTransferManager.backend;

import com.Cross_BorderDataTransferManager.backend.controller.DataTransferController;
import com.Cross_BorderDataTransferManager.backend.entity.DataTransfer;
import com.Cross_BorderDataTransferManager.backend.config.JwtUtil;
import com.Cross_BorderDataTransferManager.backend.service.DataTransferService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DataTransferController.class)
class DataTransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DataTransferService service;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private UserDetailsService userDetailsService;

    @MockBean
    private JpaMetamodelMappingContext jpaMetamodelMappingContext;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "USER")
    void getAll_shouldReturnPageOfTransfers() throws Exception {
        DataTransfer transfer = new DataTransfer();
        transfer.setId(1L);
        transfer.setSourceCountry("USA");
        transfer.setDestinationCountry("UK");
        transfer.setDataType("Personal Data");
        transfer.setStatus("Approved");

        Page<DataTransfer> page = new PageImpl<>(List.of(transfer), PageRequest.of(0, 10), 1);

        when(service.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/data-transfers")
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].sourceCountry").value("USA"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getById_shouldReturnTransfer() throws Exception {
        DataTransfer transfer = new DataTransfer();
        transfer.setId(1L);
        transfer.setSourceCountry("USA");

        when(service.findById(1L)).thenReturn(Optional.of(transfer));

        mockMvc.perform(get("/api/data-transfers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void getById_shouldReturn404WhenNotFound() throws Exception {
        when(service.findById(1L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/data-transfers/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "USER")
    void create_shouldReturnCreatedTransfer() throws Exception {
        DataTransfer input = new DataTransfer();
        input.setSourceCountry("USA");
        input.setDestinationCountry("UK");
        input.setDataType("Personal Data");
        input.setStatus("Pending");

        DataTransfer saved = new DataTransfer();
        saved.setId(1L);
        saved.setSourceCountry("USA");
        saved.setDestinationCountry("UK");
        saved.setDataType("Personal Data");
        saved.setStatus("Pending");

        when(service.save(any(DataTransfer.class))).thenReturn(saved);

        mockMvc.perform(post("/api/data-transfers")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_shouldReturnUpdatedTransfer() throws Exception {
        DataTransfer input = new DataTransfer();
        input.setSourceCountry("USA");
        input.setDestinationCountry("UK");
        input.setDataType("Personal Data");
        input.setStatus("Approved");

        DataTransfer existing = new DataTransfer();
        existing.setId(1L);

        DataTransfer updated = new DataTransfer();
        updated.setId(1L);
        updated.setSourceCountry("USA");
        updated.setDestinationCountry("UK");
        updated.setDataType("Personal Data");
        updated.setStatus("Approved");

        when(service.findById(1L)).thenReturn(Optional.of(existing));
        when(service.save(any(DataTransfer.class))).thenReturn(updated);

        mockMvc.perform(put("/api/data-transfers/1")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("Approved"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void update_shouldReturn404WhenTransferMissing() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/data-transfers/99")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isNotFound());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturnNoContent() throws Exception {
        DataTransfer existing = new DataTransfer();
        existing.setId(1L);

        when(service.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/data-transfers/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void delete_shouldAllowDemoUserRole() throws Exception {
        DataTransfer existing = new DataTransfer();
        existing.setId(1L);

        when(service.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/data-transfers/1")
                .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void delete_shouldReturn404WhenTransferMissing() throws Exception {
        when(service.findById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/data-transfers/99")
                .with(csrf()))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAll_shouldReturn401WithoutAuth() throws Exception {
        mockMvc.perform(get("/api/data-transfers"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void search_shouldReturnMatchingTransfers() throws Exception {
        DataTransfer transfer = new DataTransfer();
        transfer.setId(2L);
        transfer.setSourceCountry("Germany");
        transfer.setDestinationCountry("India");

        when(service.search("germany")).thenReturn(List.of(transfer));

        mockMvc.perform(get("/api/data-transfers/search")
                .param("q", "germany"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].sourceCountry").value("Germany"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void stats_shouldReturnKpiCounts() throws Exception {
        when(service.getStats()).thenReturn(Map.of(
                "total", 30L,
                "approved", 12L,
                "pending", 10L,
                "others", 8L
        ));

        mockMvc.perform(get("/api/data-transfers/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.total").value(30))
                .andExpect(jsonPath("$.approved").value(12));
    }

    @Test
    @WithMockUser(roles = "USER")
    void exportCsv_shouldReturnDownloadableCsv() throws Exception {
        DataTransfer transfer = new DataTransfer();
        transfer.setId(3L);
        transfer.setSourceCountry("USA");
        transfer.setDestinationCountry("Japan");
        transfer.setDataType("Financial Data");
        transfer.setStatus("Approved");

        Page<DataTransfer> page = new PageImpl<>(List.of(transfer), PageRequest.of(0, 1000), 1);
        when(service.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/data-transfers/export/csv"))
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.CONTENT_TYPE, "text/csv"))
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Financial Data")));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchFiltered_shouldReturnPageOfFilteredTransfers() throws Exception {
        DataTransfer transfer = new DataTransfer();
        transfer.setId(5L);
        transfer.setStatus("Approved");
        Page<DataTransfer> page = new PageImpl<>(List.of(transfer), PageRequest.of(0, 10), 1);
        when(service.searchWithFilters(any(), any(), any(), any(), any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/data-transfers/search/filtered")
                .param("q", "usa")
                .param("status", "Approved")
                .param("startDate", "2026-05-01T00:00:00")
                .param("endDate", "2026-05-08T23:59:59"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].status").value("Approved"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void searchFiltered_shouldReturn400ForInvalidDate() throws Exception {
        mockMvc.perform(get("/api/data-transfers/search/filtered")
                .param("startDate", "not-a-date"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "USER")
    void uploadFile_shouldRejectUnsupportedContentType() throws Exception {
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "notes.txt",
                "text/plain",
                "hello".getBytes()
        );

        mockMvc.perform(multipart("/api/data-transfers/upload")
                .file(file)
                .with(csrf()))
                .andExpect(status().isBadRequest());
    }
}
