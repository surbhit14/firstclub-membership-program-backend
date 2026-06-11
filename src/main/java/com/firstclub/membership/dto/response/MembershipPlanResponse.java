package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.MembershipPlan;
import com.firstclub.membership.enums.PlanType;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class MembershipPlanResponse {
    private Long id;
    private String name;
    private PlanType planType;
    private int durationMonths;
    private BigDecimal price;
    private BigDecimal pricePerMonth;
    private String description;

    public static MembershipPlanResponse from(MembershipPlan plan) {
        BigDecimal pricePerMonth = plan.getDurationMonths() > 1
                ? plan.getPrice().divide(BigDecimal.valueOf(plan.getDurationMonths()), 2, java.math.RoundingMode.HALF_UP)
                : plan.getPrice();

        return MembershipPlanResponse.builder()
                .id(plan.getId())
                .name(plan.getName())
                .planType(plan.getPlanType())
                .durationMonths(plan.getDurationMonths())
                .price(plan.getPrice())
                .pricePerMonth(pricePerMonth)
                .description(plan.getDescription())
                .build();
    }
}
