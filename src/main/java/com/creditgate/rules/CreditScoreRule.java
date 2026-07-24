package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.springframework.stereotype.Component;

/**
 * Checks if the applicant's credit score meets the minimum requirement of 600.
 */
@Component
public class CreditScoreRule implements EligibilityRule {

    private static final String RULE_NAME = "Credit Score Rule";
    private static final int MIN_CREDIT_SCORE = 600;

    @Override
    public RuleResult evaluate(LoanApplication application) {
        int score = application.getCreditScore();
        if (score >= MIN_CREDIT_SCORE) {
            return new RuleResult(
                    RULE_NAME,
                    true,
                    String.format("Credit score %d meets the minimum requirement of %d.", score, MIN_CREDIT_SCORE)
            );
        } else {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    String.format("Credit score %d is below the minimum requirement of %d.", score, MIN_CREDIT_SCORE)
            );
        }
    }
}
