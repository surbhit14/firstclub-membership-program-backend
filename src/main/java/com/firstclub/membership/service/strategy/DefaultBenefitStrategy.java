package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;

import java.math.BigDecimal;

/**
 * Fallback strategy used when no specific strategy is registered for a BenefitType.
 * Delegates to the standard minOrderValue check, which is correct for most benefit types.
 *
 * This is NOT a Spring @Component — it is instantiated directly by BenefitApplicationService
 * as a fallback, keeping the dispatch map exhaustive without requiring a @Component per type.
 */
public class DefaultBenefitStrategy extends MinOrderValueBenefitStrategy {

    private final BenefitType benefitType;

    public DefaultBenefitStrategy(BenefitType benefitType) {
        this.benefitType = benefitType;
    }

    @Override
    public BenefitType getSupportedBenefitType() {
        return benefitType;
    }
}
