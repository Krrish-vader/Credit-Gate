package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmploymentStabilityRuleTest {

    private final EmploymentStabilityRule rule = new EmploymentStabilityRule();

    @Test
    void shouldPassWhenEmployedAndDurationExactlyTwelveMonths() {
        LoanApplication app = LoanApplication.builder()
                .employmentStatus("EMPLOYED")
                .employmentDurationMonths(12)
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("12 months"));
    }

    @Test
    void shouldPassWhenSelfEmployedAndDurationAboveTwelveMonths() {
        LoanApplication app = LoanApplication.builder()
                .employmentStatus("SELF_EMPLOYED")
                .employmentDurationMonths(13)
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("13 months"));
    }

    @Test
    void shouldFailWhenEmployedButDurationBelowTwelveMonths() {
        LoanApplication app = LoanApplication.builder()
                .employmentStatus("EMPLOYED")
                .employmentDurationMonths(11)
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("11 months"));
    }

    @Test
    void shouldFailWhenUnemployedRegardlessOfDuration() {
        // High duration but unemployed status
        LoanApplication app = LoanApplication.builder()
                .employmentStatus("UNEMPLOYED")
                .employmentDurationMonths(24)
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("Employment status is UNEMPLOYED"));
    }

    @Test
    void shouldFailWhenUnemployedLowerCaseRegardlessOfDuration() {
        LoanApplication app = LoanApplication.builder()
                .employmentStatus("unemployed")
                .employmentDurationMonths(36)
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("Employment status is UNEMPLOYED"));
    }
}
