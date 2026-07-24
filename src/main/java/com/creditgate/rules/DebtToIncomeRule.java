package com.creditgate.rules;

import com.creditgate.entity.LoanApplication;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Checks if the applicant's Debt-To-Income (DTI) ratio is within the maximum limit of 45%.
 * Formula: Monthly Debt / Monthly Income
 */
@Component
public class DebtToIncomeRule implements EligibilityRule {

    private static final String RULE_NAME = "Debt-To-Income Rule";
    private static final BigDecimal MAX_DTI = new BigDecimal("0.45");

    @Override
    public RuleResult evaluate(LoanApplication application) {
        BigDecimal income = application.getMonthlyIncome();
        BigDecimal debt = application.getExistingMonthlyDebt();

        if (income == null || income.compareTo(BigDecimal.ZERO) <= 0) {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    "Monthly income must be greater than zero to evaluate Debt-to-Income ratio."
            );
        }

        // Calculate DTI ratio (debt / income)
        BigDecimal dtiRatio = debt.divide(income, 4, RoundingMode.HALF_UP);
        BigDecimal dtiPercentage = dtiRatio.multiply(new BigDecimal("100")).setScale(2, RoundingMode.HALF_UP);

        boolean passed = dtiRatio.compareTo(MAX_DTI) <= 0;

        if (passed) {
            return new RuleResult(
                    RULE_NAME,
                    true,
                    String.format("Debt-to-income ratio of %s%% is within the maximum allowed threshold of 45%%.", dtiPercentage.toPlainString())
            );
        } else {
            return new RuleResult(
                    RULE_NAME,
                    false,
                    String.format("Debt-to-income ratio of %s%% exceeds the maximum allowed threshold of 45%%.", dtiPercentage.toPlainString())
            );
        }
    }
}
