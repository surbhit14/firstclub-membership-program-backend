package com.firstclub.membership.service.strategy;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;

import java.math.BigDecimal;

/**
 * Strategy interface for evaluating whether a benefit applies for a given order context.
 *
 * Each BenefitType gets its own implementation, keeping benefit logic isolated and
 * extensible — adding a new BenefitType with custom semantics requires only one new
 * @Component, no changes to BenefitApplicationService.
 *
 * Mirrors TierEvaluationStrategy which does the same for CriteriaType.
 */
public interface BenefitEvaluationStrategy {
    BenefitType getSupportedBenefitType();

    /**
     * Returns true if this benefit applies for the given order amount.
     * Implementations can inspect any field of TierBenefit (value, minOrderValue, etc.)
     * and any order context they need.
     */
    boolean isApplicable(TierBenefit benefit, BigDecimal orderAmount);
}
