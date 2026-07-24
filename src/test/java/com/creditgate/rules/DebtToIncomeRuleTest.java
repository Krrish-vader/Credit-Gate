package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class DebtToIncomeRuleTest {

    private final DebtToIncomeRule rule = new DebtToIncomeRule();

    @Test
    void shouldPassWhenDtiIsJustBelowThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("10000.00"))
                .existingMonthlyDebt(new BigDecimal("4490.00")) // 44.9% DTI
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("44.90%"));
    }

    @Test
    void shouldPassWhenDtiIsExactlyAtThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("10000.00"))
                .existingMonthlyDebt(new BigDecimal("4500.00")) // 45.0% DTI
                .build();

        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("45.00%"));
    }

    @Test
    void shouldFailWhenDtiIsJustAboveThreshold() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(new BigDecimal("10000.00"))
                .existingMonthlyDebt(new BigDecimal("4510.00")) // 45.1% DTI
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("45.10%"));
    }

    @Test
    void shouldFailWhenMonthlyIncomeIsZero() {
        LoanApplication app = LoanApplication.builder()
                .monthlyIncome(BigDecimal.ZERO)
                .existingMonthlyDebt(new BigDecimal("500.00"))
                .build();

        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertTrue(result.reason().contains("Monthly income must be greater than zero"));
    }
}
