package com.creditgate.service;

import com.creditgate.entity.InterestRateTier;
import java.math.BigDecimal;

/**
 * Result representing the assigned interest rate tier and interest rate.
 */
public record RateTierResult(InterestRateTier tier, BigDecimal interestRate) {
}
