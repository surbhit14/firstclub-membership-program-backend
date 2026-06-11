package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.TierBenefit;
import com.firstclub.membership.enums.BenefitType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class TierBenefitResponse {
    private BenefitType benefitType;
    private double value;
    private BigDecimal minOrderValue;  // null means benefit always applies
    private String description;

    public static TierBenefitResponse from(TierBenefit benefit) {
        return TierBenefitResponse.builder()
                .benefitType(benefit.getBenefitType())
                .value(benefit.getValue())
                .minOrderValue(benefit.getMinOrderValue())
                .description(benefit.getDescription())
                .build();
    }
}
