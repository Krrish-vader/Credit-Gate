package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.springframework.stereotype.Component;

/**
 * Checks if the applicant has stable employment:
 * 1. Employment status must not be 'UNEMPLOYED'.
 * 2. Employment duration must be at least 12 months.
 */
@Component
public class EmploymentStabilityRule implements EligibilityRule {

    private static final String RULE_NAME = "Employment Stability Rule";
    private static final int MIN_DURATION_MONTHS = 12;
    private static final String STATUS_UNEMPLOYED = "UNEMPLOYED";

    @Override
    public RuleResult evaluate(LoanApplication application) {
        String status = application.getEmploymentStatus();
        int duration = application.getEmploymentDurationMonths();

        if (status == null || STATUS_UNEMPLOYED.equalsIgnoreCase(status.trim())) {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    "Employment status is UNEMPLOYED, which does not meet the stability requirement."
            );
        }

        if (duration >= MIN_DURATION_MONTHS) {
            return new RuleResult(
                    RULE_NAME,
                    true,
                    String.format("Employment status is %s and duration of %d months meets the minimum requirement of %d months.",
                            status, duration, MIN_DURATION_MONTHS)
            );
        } else {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    String.format("Employment duration of %d months is below the minimum required %d months.",
                            duration, MIN_DURATION_MONTHS)
            );
        }
    }
}
