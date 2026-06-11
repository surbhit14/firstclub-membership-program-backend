package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.UserMembership;
import com.firstclub.membership.enums.MembershipStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Getter
@Builder
public class UserMembershipResponse {
    private Long id;
    private Long userId;
    private String userName;
    private MembershipPlanResponse plan;
    private MembershipTierResponse tier;
    private MembershipStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private long daysRemaining;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static UserMembershipResponse from(UserMembership membership) {
        long daysRemaining = membership.getStatus() == MembershipStatus.ACTIVE
                ? Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), membership.getEndDate()))
                : 0;

        return UserMembershipResponse.builder()
                .id(membership.getId())
                .userId(membership.getUser().getId())
                .userName(membership.getUser().getName())
                .plan(MembershipPlanResponse.from(membership.getPlan()))
                .tier(MembershipTierResponse.from(membership.getTier()))
                .status(membership.getStatus())
                .startDate(membership.getStartDate())
                .endDate(membership.getEndDate())
                .daysRemaining(daysRemaining)
                .createdAt(membership.getCreatedAt())
                .updatedAt(membership.getUpdatedAt())
                .build();
    }
}
