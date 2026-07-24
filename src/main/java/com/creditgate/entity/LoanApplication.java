package com.creditgate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA Entity representing a loan application and its evaluation outcomes.
 */
@Entity
@Table(name = "loan_applications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoanApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "monthly_income", nullable = false, precision = 15, scale = 2)
    private BigDecimal monthlyIncome;

    @Column(name = "credit_score", nullable = false)
    private Integer creditScore;

    @Column(name = "existing_monthly_debt", nullable = false, precision = 15, scale = 2)
    private BigDecimal existingMonthlyDebt;

    @Column(name = "requested_loan_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal requestedLoanAmount;

    @Column(name = "employment_duration_months", nullable = false)
    private Integer employmentDurationMonths;

    @Column(name = "employment_status", nullable = false, length = 50)
    private String employmentStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private DecisionStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "interest_rate_tier", length = 20)
    private InterestRateTier interestRateTier;

    @Column(name = "interest_rate", precision = 5, scale = 2)
    private BigDecimal interestRate;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "loanApplication", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AuditRecord> auditRecords = new ArrayList<>();
}
