package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.MembershipTier;
import com.firstclub.membership.enums.CriteriaLogic;
import com.firstclub.membership.enums.TierLevel;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class MembershipTierResponse {
    private Long id;
    private String name;
    private TierLevel tierLevel;
    private String description;
    private CriteriaLogic criteriaLogic;
    private List<TierBenefitResponse> benefits;

    public static MembershipTierResponse from(MembershipTier tier) {
        return MembershipTierResponse.builder()
                .id(tier.getId())
                .name(tier.getName())
                .tierLevel(tier.getTierLevel())
                .description(tier.getDescription())
                .criteriaLogic(tier.getCriteriaLogic())
                .benefits(tier.getBenefits().stream()
                        .map(TierBenefitResponse::from)
                        .collect(Collectors.toList()))
                .build();
    }
}
