package com.creditgate.service;

import com.creditgate.entity.InterestRateTier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Service to determine interest rate tiers and interest rates based on credit score bands.
 * NOTE: The interest rates and bands configured here are illustrative example values
 * for this portfolio project and do not represent real financial or lending advice.
 */
@Service
public class RateTierService {

    public RateTierResult determineRateAndTier(int creditScore) {
        if (creditScore >= 750) {
            return new RateTierResult(InterestRateTier.TIER_A, new BigDecimal("5.50"));
        } else if (creditScore >= 700) {
            return new RateTierResult(InterestRateTier.TIER_B, new BigDecimal("7.00"));
        } else if (creditScore >= 600) {
            return new RateTierResult(InterestRateTier.TIER_C, new BigDecimal("9.50"));
        } else {
            return new RateTierResult(InterestRateTier.NONE, null);
        }
    }
}
