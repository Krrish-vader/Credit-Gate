package com.creditgate.service;

import com.creditgate.entity.AuditRecord;
import com.creditgate.entity.DecisionStatus;
import com.creditgate.entity.InterestRateTier;
import com.creditgate.entity.LoanApplication;
import com.creditgate.repository.LoanApplicationRepository;
import com.creditgate.rules.EligibilityRule;
import com.creditgate.rules.RuleResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service that orchestrates the execution of all pluggable eligibility rules against a loan application,
 * builds the decision audit trail, determines interest rate tiering, and persists the final decision.
 *
 * <p><b>Spring Bean Injection Mechanism:</b>
 * Spring Boot automatically scans for and collects all Spring-managed beans (@Component, @Service, etc.)
 * that implement the {@link EligibilityRule} interface and injects them into the constructor's {@code List<EligibilityRule>}.
 * This pluggable design means that adding a new rule only requires creating a new class implementing
 * {@code EligibilityRule} and annotating it with {@code @Component}. The orchestrator automatically includes it in
 * subsequent evaluations without needing any code changes here.
 * </p>
 */
@Service
@RequiredArgsConstructor
public class LoanEvaluationService {

    private final List<EligibilityRule> rules;
    private final RateTierService rateTierService;
    private final LoanApplicationRepository loanApplicationRepository;

    /**
     * Evaluates a loan application against all registered rules, assigns interest rate, and persists the result.
     *
     * @param application the loan application input
     * @return the saved LoanApplication entity containing the final status and audit trail
     */
    @Transactional
    public LoanApplication evaluateAndSave(LoanApplication application) {
        application.setCreatedAt(LocalDateTime.now());
        
        boolean overallPassed = true;
        LocalDateTime evaluationTime = LocalDateTime.now();

        // Run the application through every registered rule
        for (EligibilityRule rule : rules) {
            RuleResult result = rule.evaluate(application);
            
            // Collect the outcome as an AuditRecord
            AuditRecord auditRecord = AuditRecord.builder()
                    .loanApplication(application)
                    .ruleName(result.ruleName())
                    .passed(result.passed())
                    .reason(result.reason())
                    .evaluatedAt(evaluationTime)
                    .build();
            
            application.getAuditRecords().add(auditRecord);

            // If any rule fails, the overall application is rejected (we continue evaluating remaining rules)
            if (!result.passed()) {
                overallPassed = false;
            }
        }

        // Determine status and interest rate tiering
        if (overallPassed) {
            application.setStatus(DecisionStatus.APPROVED);
            RateTierResult tierResult = rateTierService.determineRateAndTier(application.getCreditScore());
            application.setInterestRateTier(tierResult.tier());
            application.setInterestRate(tierResult.interestRate());
        } else {
            application.setStatus(DecisionStatus.REJECTED);
            application.setInterestRateTier(InterestRateTier.NONE);
            application.setInterestRate(null);
        }

        // Persist the application and its cascaded audit trail records
        return loanApplicationRepository.save(application);
    }
}
