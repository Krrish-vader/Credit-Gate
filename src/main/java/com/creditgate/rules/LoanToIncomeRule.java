package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Checks if the requested loan amount relative to the annual income is within the maximum multiple of 3.0x.
 * Formula: Loan Amount / (Monthly Income * 12)
 */
@Component
public class LoanToIncomeRule implements EligibilityRule {

    private static final String RULE_NAME = "Loan-To-Income Rule";
    private static final BigDecimal MAX_LTI = new BigDecimal("3.0");
    private static final BigDecimal MONTHS_IN_YEAR = new BigDecimal("12");

    @Override
    public RuleResult evaluate(LoanApplication application) {
        BigDecimal monthlyIncome = application.getMonthlyIncome();
        BigDecimal loanAmount = application.getRequestedLoanAmount();

        if (monthlyIncome == null || monthlyIncome.compareTo(BigDecimal.ZERO) <= 0) {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    "Monthly income must be greater than zero to evaluate Loan-to-Income ratio."
            );
        }

        // Convert monthly income to annual income: Annual Income = Monthly Income * 12
        BigDecimal annualIncome = monthlyIncome.multiply(MONTHS_IN_YEAR);

        // Calculate LTI ratio: Loan Amount / Annual Income
        BigDecimal ltiRatio = loanAmount.divide(annualIncome, 6, RoundingMode.HALF_UP);
        BigDecimal formattedLti = ltiRatio.setScale(2, RoundingMode.HALF_UP);

        boolean passed = ltiRatio.compareTo(MAX_LTI) <= 0;

        if (passed) {
            return new RuleResult(
                    RULE_NAME,
                    true,
                    String.format("Requested loan amount of %s is %sx of annual income (%s), which is within the maximum limit of 3.0x.",
                            loanAmount.toPlainString(), formattedLti.toPlainString(), annualIncome.toPlainString())
            );
        } else {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    String.format("Requested loan amount of %s is %sx of annual income (%s), which exceeds the maximum limit of 3.0x.",
                            loanAmount.toPlainString(), formattedLti.toPlainString(), annualIncome.toPlainString())
            );
        }
    }
}
