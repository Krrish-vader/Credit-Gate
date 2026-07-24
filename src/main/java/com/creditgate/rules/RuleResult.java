package com.creditgate.rules;

/**
 * An immutable record representing the evaluation outcome of a single eligibility rule.
 */
public record RuleResult(String ruleName, boolean passed, String reason) {
}
