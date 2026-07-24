package com.creditgate.dto;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * Request payload containing input fields for submitting a new loan application.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplicationRequest {

    @NotNull(message = "Monthly income is required.")
    @DecimalMin(value = "0.01", message = "Monthly income must be greater than zero.")
    private BigDecimal monthlyIncome;

    @NotNull(message = "Credit score is required.")
    @Min(value = 300, message = "Credit score must be at least 300.")
    @Max(value = 850, message = "Credit score cannot exceed 850.")
    private Integer creditScore;

    @NotNull(message = "Existing monthly debt is required.")
    @DecimalMin(value = "0.00", message = "Existing monthly debt cannot be negative.")
    private BigDecimal existingMonthlyDebt;

    @NotNull(message = "Requested loan amount is required.")
    @DecimalMin(value = "0.01", message = "Requested loan amount must be greater than zero.")
    private BigDecimal requestedLoanAmount;

    @NotNull(message = "Employment duration in months is required.")
    @Min(value = 0, message = "Employment duration cannot be negative.")
    private Integer employmentDurationMonths;

    @NotBlank(message = "Employment status is required (e.g. EMPLOYED, SELF_EMPLOYED, UNEMPLOYED).")
    private String employmentStatus;
}
