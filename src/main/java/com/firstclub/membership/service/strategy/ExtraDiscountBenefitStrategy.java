package com.firstclub.membership.service.strategy;

import com.firstclub.membership.enums.BenefitType;
import org.springframework.stereotype.Component;

/**
 * EXTRA_DISCOUNT applies when orderAmount >= minOrderValue (or no minimum).
 * The discount percentage is stored in TierBenefit.value (e.g. 5.0, 10.0, 20.0).
 *
 * To restrict to specific categories, the caller can pass category context via
 * an overloaded isApplicable — extend this strategy when that requirement arrives.
 */
@Component
public class ExtraDiscountBenefitStrategy extends MinOrderValueBenefitStrategy {
    @Override
    public BenefitType getSupportedBenefitType() {
        return BenefitType.EXTRA_DISCOUNT;
    }
}
