package com.creditgate.controller;

import com.creditgate.dto.LoanApplicationRequest;
import com.creditgate.entity.AuditRecord;
import com.creditgate.entity.DecisionStatus;
import com.creditgate.entity.InterestRateTier;
import com.creditgate.entity.LoanApplication;
import com.creditgate.repository.LoanApplicationRepository;
import com.creditgate.service.LoanEvaluationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LoanApplicationController.class)
class LoanApplicationControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LoanEvaluationService loanEvaluationService;

    @MockBean
    private LoanApplicationRepository loanApplicationRepository;

    @Test
    void shouldSubmitApplicationSuccessfullyAndReturn201Created() throws Exception {
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .monthlyIncome(new BigDecimal("5000.00"))
                .creditScore(720)
                .existingMonthlyDebt(new BigDecimal("1000.00"))
                .requestedLoanAmount(new BigDecimal("10000.00"))
                .employmentDurationMonths(24)
                .employmentStatus("EMPLOYED")
                .build();

        UUID generatedId = UUID.randomUUID();
        LoanApplication savedEntity = LoanApplication.builder()
                .id(generatedId)
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .existingMonthlyDebt(request.getExistingMonthlyDebt())
                .requestedLoanAmount(request.getRequestedLoanAmount())
                .employmentDurationMonths(request.getEmploymentDurationMonths())
                .employmentStatus(request.getEmploymentStatus())
                .status(DecisionStatus.APPROVED)
                .interestRateTier(InterestRateTier.TIER_B)
                .interestRate(new BigDecimal("7.00"))
                .createdAt(LocalDateTime.now())
                .auditRecords(new ArrayList<>())
                .build();

        // Add mock audit records for response conversion verification
        savedEntity.getAuditRecords().add(AuditRecord.builder()
                .id(UUID.randomUUID())
                .loanApplication(savedEntity)
                .ruleName("Credit Score Rule")
                .passed(true)
                .reason("Credit score meets requirement.")
                .evaluatedAt(LocalDateTime.now())
                .build());

        when(loanEvaluationService.evaluateAndSave(any(LoanApplication.class))).thenReturn(savedEntity);

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(generatedId.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.interestRateTier").value("TIER_B"))
                .andExpect(jsonPath("$.interestRate").value(7.00))
                .andExpect(jsonPath("$.auditTrail[0].ruleName").value("Credit Score Rule"))
                .andExpect(jsonPath("$.auditTrail[0].passed").value(true));
    }

    @Test
    void shouldReturn400BadRequestWhenInputsAreInvalid() throws Exception {
        // Submit payload violating multiple constraints
        LoanApplicationRequest request = LoanApplicationRequest.builder()
                .monthlyIncome(new BigDecimal("-100.00")) // Invalid: must be positive
                .creditScore(200) // Invalid: below 300
                .existingMonthlyDebt(new BigDecimal("-50.00")) // Invalid: cannot be negative
                .requestedLoanAmount(new BigDecimal("0.00")) // Invalid: must be positive
                .employmentDurationMonths(-3) // Invalid: cannot be negative
                .employmentStatus("") // Invalid: cannot be blank
                .build();

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Validation failed for one or more fields."))
                .andExpect(jsonPath("$.details.monthlyIncome").exists())
                .andExpect(jsonPath("$.details.creditScore").exists())
                .andExpect(jsonPath("$.details.existingMonthlyDebt").exists())
                .andExpect(jsonPath("$.details.requestedLoanAmount").exists())
                .andExpect(jsonPath("$.details.employmentDurationMonths").exists())
                .andExpect(jsonPath("$.details.employmentStatus").exists());
    }

    @Test
    void shouldReturn200OkWhenFetchingExistingApplication() throws Exception {
        UUID id = UUID.randomUUID();
        LoanApplication entity = LoanApplication.builder()
                .id(id)
                .monthlyIncome(new BigDecimal("5000.00"))
                .creditScore(720)
                .existingMonthlyDebt(new BigDecimal("1000.00"))
                .requestedLoanAmount(new BigDecimal("10000.00"))
                .employmentDurationMonths(24)
                .employmentStatus("EMPLOYED")
                .status(DecisionStatus.APPROVED)
                .interestRateTier(InterestRateTier.TIER_B)
                .interestRate(new BigDecimal("7.00"))
                .createdAt(LocalDateTime.now())
                .auditRecords(new ArrayList<>())
                .build();

        when(loanApplicationRepository.findById(id)).thenReturn(Optional.of(entity));

        mockMvc.perform(get("/api/applications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(id.toString()))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturn404NotFoundWhenApplicationDoesNotExist() throws Exception {
        UUID id = UUID.randomUUID();
        when(loanApplicationRepository.findById(id)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/applications/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Loan application not found with ID: " + id));
    }
}
