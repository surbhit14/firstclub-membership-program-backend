package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;

import java.math.BigDecimal;

/**
 * Abstract base for benefit strategies whose only condition is a minimum order value.
 *
 * Logic: if minOrderValue is null → no minimum set → always applicable.
 *        if minOrderValue is set  → applicable only when orderAmount >= minOrderValue.
 *
 * Example: Silver FREE_DELIVERY has minOrderValue=₹499. An order of ₹300 → isApplicable=false.
 *          Gold FREE_DELIVERY has minOrderValue=null → always applicable.
 *
 * Concrete subclasses only implement getSupportedBenefitType() and inherit this check.
 * To add benefit-specific logic, override isApplicable() and call super.isApplicable() first.
 */
public abstract class MinOrderValueBenefitStrategy implements BenefitEvaluationStrategy {

    @Override
    public boolean isApplicable(TierBenefit benefit, BigDecimal orderAmount) {
        // null minOrderValue means no threshold — benefit always applies
        return benefit.getMinOrderValue() == null
                || orderAmount.compareTo(benefit.getMinOrderValue()) >= 0;
    }
}
