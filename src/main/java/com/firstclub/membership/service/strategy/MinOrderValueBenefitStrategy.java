package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;

import java.math.BigDecimal;

/**
 * Base strategy for benefits whose only condition is a minimum order value.
 * Subclass this for any BenefitType that needs additional checks on top.
 *
 * Covers: FREE_DELIVERY, EXTRA_DISCOUNT, EXCLUSIVE_DEALS,
 *         EARLY_SALE_ACCESS, PRIORITY_SUPPORT, EXCLUSIVE_COUPONS, FASTER_DELIVERY.
 */
public abstract class MinOrderValueBenefitStrategy implements BenefitEvaluationStrategy {

    @Override
    public boolean isApplicable(TierBenefit benefit, BigDecimal orderAmount) {
        return benefit.getMinOrderValue() == null
                || orderAmount.compareTo(benefit.getMinOrderValue()) >= 0;
    }
}
