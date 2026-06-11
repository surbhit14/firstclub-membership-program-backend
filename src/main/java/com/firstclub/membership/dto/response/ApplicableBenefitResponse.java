package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * Read-only response DTO for a benefit + its applicability verdict for a specific order.
 * conditionMet is computed by BenefitApplicationService (via BenefitEvaluationStrategy),
 * not here — DTOs carry data, they don't evaluate business rules.
 */
@Getter
@Builder
public class ApplicableBenefitResponse {
    private BenefitType benefitType;
    private double value;
    private BigDecimal minOrderValue;
    private String description;
    private boolean conditionMet;

    public static ApplicableBenefitResponse from(TierBenefit benefit, boolean conditionMet) {
        return ApplicableBenefitResponse.builder()
                .benefitType(benefit.getBenefitType())
                .value(benefit.getValue())
                .minOrderValue(benefit.getMinOrderValue())
                .description(benefit.getDescription())
                .conditionMet(conditionMet)
                .build();
    }
}
