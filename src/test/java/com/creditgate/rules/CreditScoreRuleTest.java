package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CreditScoreRuleTest {

    private final CreditScoreRule rule = new CreditScoreRule();

    @Test
    void shouldPassWhenCreditScoreIsExactlyAtThreshold() {
        LoanApplication app = LoanApplication.builder().creditScore(600).build();
        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertEquals("Credit Score Rule", result.ruleName());
        assertTrue(result.reason().contains("meets the minimum requirement"));
    }

    @Test
    void shouldPassWhenCreditScoreIsAboveThreshold() {
        LoanApplication app = LoanApplication.builder().creditScore(601).build();
        RuleResult result = rule.evaluate(app);

        assertTrue(result.passed());
        assertTrue(result.reason().contains("meets the minimum requirement"));
    }

    @Test
    void shouldFailWhenCreditScoreIsBelowThreshold() {
        LoanApplication app = LoanApplication.builder().creditScore(599).build();
        RuleResult result = rule.evaluate(app);

        assertFalse(result.passed());
        assertEquals("Credit Score Rule", result.ruleName());
        assertTrue(result.reason().contains("is below the minimum requirement"));
    }
}
