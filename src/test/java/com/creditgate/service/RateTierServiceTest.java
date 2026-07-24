package com.creditgate.service;

import com.creditgate.entity.InterestRateTier;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class RateTierServiceTest {

    private final RateTierService service = new RateTierService();

    @Test
    void shouldReturnNoneAndNullRateForScoreBelowSixHundred() {
        RateTierResult result = service.determineRateAndTier(599);
        assertEquals(InterestRateTier.NONE, result.tier());
        assertNull(result.interestRate());
    }

    @Test
    void shouldReturnTierCAndCorrectRateForScoreExactlySixHundred() {
        RateTierResult result = service.determineRateAndTier(600);
        assertEquals(InterestRateTier.TIER_C, result.tier());
        assertEquals(new BigDecimal("9.50"), result.interestRate());
    }

    @Test
    void shouldReturnTierCAndCorrectRateForScoreAtUpperBoundaryOfTierC() {
        RateTierResult result = service.determineRateAndTier(699);
        assertEquals(InterestRateTier.TIER_C, result.tier());
        assertEquals(new BigDecimal("9.50"), result.interestRate());
    }

    @Test
    void shouldReturnTierBAndCorrectRateForScoreExactlySevenHundred() {
        RateTierResult result = service.determineRateAndTier(700);
        assertEquals(InterestRateTier.TIER_B, result.tier());
        assertEquals(new BigDecimal("7.00"), result.interestRate());
    }

    @Test
    void shouldReturnTierBAndCorrectRateForScoreAtUpperBoundaryOfTierB() {
        RateTierResult result = service.determineRateAndTier(749);
        assertEquals(InterestRateTier.TIER_B, result.tier());
        assertEquals(new BigDecimal("7.00"), result.interestRate());
    }

    @Test
    void shouldReturnTierAAndCorrectRateForScoreExactlySevenFifty() {
        RateTierResult result = service.determineRateAndTier(750);
        assertEquals(InterestRateTier.TIER_A, result.tier());
        assertEquals(new BigDecimal("5.50"), result.interestRate());
    }

    @Test
    void shouldReturnTierAAndCorrectRateForScoreWellAboveSevenFifty() {
        RateTierResult result = service.determineRateAndTier(800);
        assertEquals(InterestRateTier.TIER_A, result.tier());
        assertEquals(new BigDecimal("5.50"), result.interestRate());
    }
}
