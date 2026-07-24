package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;

/**
 * Interface that all pluggable loan eligibility rules must implement.
 */
public interface EligibilityRule {
    /**
     * Evaluates a loan application against the rule's criteria.
     *
     * @param application the loan application to evaluate
     * @return the result of the evaluation, including pass/fail status and reasoning
     */
    RuleResult evaluate(LoanApplication application);
}
