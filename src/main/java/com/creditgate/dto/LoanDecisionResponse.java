package com.creditgate.dto;

import com.creditgate.entity.DecisionStatus;
import com.creditgate.entity.InterestRateTier;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Response payload representing the complete evaluation decision, status,
 * interest rate tiering, and the detailed audit trail.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanDecisionResponse {
    private UUID id;
    private BigDecimal monthlyIncome;
    private Integer creditScore;
    private BigDecimal existingMonthlyDebt;
    private BigDecimal requestedLoanAmount;
    private Integer employmentDurationMonths;
    private String employmentStatus;
    private DecisionStatus status;
    private InterestRateTier interestRateTier;
    private BigDecimal interestRate;
    private LocalDateTime createdAt;
    private List<RuleEvaluationDto> auditTrail;
}
