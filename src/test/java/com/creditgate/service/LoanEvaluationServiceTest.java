package com.creditgate.service;

import com.creditgate.entity.DecisionStatus;
import com.creditgate.entity.InterestRateTier;
import com.creditgate.entity.LoanApplication;
import com.creditgate.repository.LoanApplicationRepository;
import com.creditgate.rules.EligibilityRule;
import com.creditgate.rules.RuleResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanEvaluationServiceTest {

    @Mock
    private EligibilityRule rule1;

    @Mock
    private EligibilityRule rule2;

    @Mock
    private RateTierService rateTierService;

    @Mock
    private LoanApplicationRepository repository;

    private LoanEvaluationService service;

    @BeforeEach
    void setUp() {
        // Construct the service by injecting mocked rules list
        service = new LoanEvaluationService(Arrays.asList(rule1, rule2), rateTierService, repository);
    }

    @Test
    void shouldApproveApplicationWhenAllRulesPass() {
        LoanApplication app = LoanApplication.builder()
                .creditScore(720)
                .monthlyIncome(new BigDecimal("5000.00"))
                .existingMonthlyDebt(new BigDecimal("1000.00"))
                .requestedLoanAmount(new BigDecimal("10000.00"))
                .employmentDurationMonths(24)
                .employmentStatus("EMPLOYED")
                .build();

        when(rule1.evaluate(app)).thenReturn(new RuleResult("Rule 1", true, "Passed 1"));
        when(rule2.evaluate(app)).thenReturn(new RuleResult("Rule 2", true, "Passed 2"));
        when(rateTierService.determineRateAndTier(720)).thenReturn(new RateTierResult(InterestRateTier.TIER_B, new BigDecimal("7.00")));
        when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanApplication result = service.evaluateAndSave(app);

        assertNotNull(result);
        assertEquals(DecisionStatus.APPROVED, result.getStatus());
        assertEquals(InterestRateTier.TIER_B, result.getInterestRateTier());
        assertEquals(new BigDecimal("7.00"), result.getInterestRate());
        assertEquals(2, result.getAuditRecords().size());
        assertTrue(result.getAuditRecords().get(0).isPassed());
        assertEquals("Rule 1", result.getAuditRecords().get(0).getRuleName());
        assertTrue(result.getAuditRecords().get(1).isPassed());
        assertEquals("Rule 2", result.getAuditRecords().get(1).getRuleName());

        verify(rule1).evaluate(app);
        verify(rule2).evaluate(app);
        verify(rateTierService).determineRateAndTier(720);
        verify(repository).save(app);
    }

    @Test
    void shouldRejectApplicationAndNotShortCircuitWhenAnyRuleFails() {
        LoanApplication app = LoanApplication.builder()
                .creditScore(580)
                .monthlyIncome(new BigDecimal("5000.00"))
                .existingMonthlyDebt(new BigDecimal("4000.00")) // high debt, likely DTI fail
                .requestedLoanAmount(new BigDecimal("10000.00"))
                .employmentDurationMonths(24)
                .employmentStatus("EMPLOYED")
                .build();

        // rule1 fails, rule2 passes
        when(rule1.evaluate(app)).thenReturn(new RuleResult("Rule 1", false, "Failed 1"));
        when(rule2.evaluate(app)).thenReturn(new RuleResult("Rule 2", true, "Passed 2"));
        when(repository.save(any(LoanApplication.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LoanApplication result = service.evaluateAndSave(app);

        assertNotNull(result);
        assertEquals(DecisionStatus.REJECTED, result.getStatus());
        assertEquals(InterestRateTier.NONE, result.getInterestRateTier());
        assertNull(result.getInterestRate());
        assertEquals(2, result.getAuditRecords().size());
        assertFalse(result.getAuditRecords().get(0).isPassed());
        assertTrue(result.getAuditRecords().get(1).isPassed());

        // Verifying non-short-circuiting logic: both rules must be run!
        verify(rule1).evaluate(app);
        verify(rule2).evaluate(app);
        verifyNoInteractions(rateTierService); // Should not compute rates for rejected applications
        verify(repository).save(app);
    }
}
