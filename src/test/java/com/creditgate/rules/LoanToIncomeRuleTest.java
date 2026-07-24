package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoanToIncomeRuleTest {

    private final LoanToIncomeRule rule = new LoanToIncomeRule();

    @Test
    void shouldPassWhenLtiIsJustBelowThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("5000.00")) // Annual = 60,000
                .requestedLoanAmount(new BigDecimal("179400.00")) // LTI = 2.99
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("2.99x"));
    }

    @Test
    void shouldPassWhenLtiIsExactlyAtThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("5000.00")) // Annual = 60,000
                .requestedLoanAmount(new BigDecimal("180000.00")) // LTI = 3.00
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("3.00x"));
    }

    @Test
    void shouldFailWhenLtiIsJustAboveThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("5000.00")) // Annual = 60,000
                .requestedLoanAmount(new BigDecimal("180600.00")) // LTI = 3.01
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("3.01x"));
    }

    @Test
    void shouldCorrectlyConvertMonthlyIncomeToAnnualIncome() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("4500.00")) // Annual should be 4500 * 12 = 54,000
                .requestedLoanAmount(new BigDecimal("162000.00")) // Exactly 3.0x of 54,000
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        // Assert that the reason string explicitly mentions the annual income calculation of 54000.00
        assertTrue(result.reason().contains("annual income (54000.00)"), 
                "The audit reason must contain the converted annual income of 54,000");
    }

    @Test
    void shouldFailWhenMonthlyIncomeIsZero() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(BigDecimal.ZERO)
                .requestedLoanAmount(new BigDecimal("10000.00"))
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("Monthly income must be greater than zero"));
    }
}
