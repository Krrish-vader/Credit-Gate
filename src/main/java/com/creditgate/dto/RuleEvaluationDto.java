package com.creditgate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing the audit result of an individual eligibility rule evaluation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RuleEvaluationDto {
    private String ruleName;
    private boolean passed;
    private String reason;
    private LocalDateTime evaluatedAt;
}
