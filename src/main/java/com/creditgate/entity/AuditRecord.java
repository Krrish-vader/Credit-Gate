package com.creditgate.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * JPA Entity representing the evaluation audit record of a single eligibility rule.
 */
@Entity
@Table(name = "audit_records")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "loan_application_id", nullable = false)
    private LoanApplication loanApplication;

    @Column(name = "rule_name", nullable = false, length = 100)
    private String ruleName;

    @Column(name = "passed", nullable = false)
    private boolean passed;

    @Column(name = "reason", nullable = false, length = 255)
    private String reason;

    @Column(name = "evaluated_at", nullable = false)
    private LocalDateTime evaluatedAt;
}
