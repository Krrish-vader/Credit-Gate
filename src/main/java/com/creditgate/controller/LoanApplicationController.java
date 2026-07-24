package com.creditgate.controller;

import com.creditgate.dto.LoanApplicationRequest;
import com.creditgate.dto.LoanDecisionResponse;
import com.creditgate.dto.RuleEvaluationDto;
import com.creditgate.entity.LoanApplication;
import com.creditgate.exception.ResourceNotFoundException;
import com.creditgate.repository.LoanApplicationRepository;
import com.creditgate.service.LoanEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * REST controller exposing endpoints to submit and retrieve loan application decisions.
 */
@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Loan Applications", description = "Endpoints for submitting loan applications and retrieving audit decision records")
public class LoanApplicationController {

    private final LoanEvaluationService loanEvaluationService;
    private final LoanApplicationRepository loanApplicationRepository;

    @PostMapping
    @Operation(summary = "Submit a loan application", description = "Submits a new loan application, runs the eligibility rules engine, assigns interest tiers, and records an audit trail.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan application evaluated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid request payload")
    })
    public ResponseEntity<LoanDecisionResponse> submitApplication(@Valid @RequestBody LoanApplicationRequest request) {
        // Map request DTO to transient entity object
        LoanApplication application = LoanApplication.builder()
                .monthlyIncome(request.getMonthlyIncome())
                .creditScore(request.getCreditScore())
                .existingMonthlyDebt(request.getExistingMonthlyDebt())
                .requestedLoanAmount(request.getRequestedLoanAmount())
                .employmentDurationMonths(request.getEmploymentDurationMonths())
                .employmentStatus(request.getEmploymentStatus())
                .build();

        // Run evaluation and persist decision
        LoanApplication evaluated = loanEvaluationService.evaluateAndSave(application);

        // Return decision response mapped to response DTO
        return new ResponseEntity<>(mapToResponse(evaluated), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get loan application decision by ID", description = "Retrieves the final status, interest tiering, and full step-by-step rule audit trail of a past application.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan application retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Loan application not found")
    })
    public ResponseEntity<LoanDecisionResponse> getApplicationDecision(@PathVariable UUID id) {
        LoanApplication application = loanApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Loan application not found with ID: " + id));

        return ResponseEntity.ok(mapToResponse(application));
    }

    /**
     * Map internal LoanApplication entity + AuditRecords list to LoanDecisionResponse DTO.
     */
    private LoanDecisionResponse mapToResponse(LoanApplication entity) {
        List<RuleEvaluationDto> auditTrail = entity.getAuditRecords().stream()
                .map(audit -> RuleEvaluationDto.builder()
                        .ruleName(audit.getRuleName())
                        .passed(audit.isPassed())
                        .reason(audit.getReason())
                        .evaluatedAt(audit.getEvaluatedAt())
                        .build())
                .toList();

        return LoanDecisionResponse.builder()
                .id(entity.getId())
                .monthlyIncome(entity.getMonthlyIncome())
                .creditScore(entity.getCreditScore())
                .existingMonthlyDebt(entity.getExistingMonthlyDebt())
                .requestedLoanAmount(entity.getRequestedLoanAmount())
                .employmentDurationMonths(entity.getEmploymentDurationMonths())
                .employmentStatus(entity.getEmploymentStatus())
                .status(entity.getStatus())
                .interestRateTier(entity.getInterestRateTier())
                .interestRate(entity.getInterestRate())
                .createdAt(entity.getCreatedAt())
                .auditTrail(auditTrail)
                .build();
    }
}
