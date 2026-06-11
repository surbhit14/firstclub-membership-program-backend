package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.Order;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private BigDecimal totalAmount;
    private String description;
    // Tier active at order time — null for non-members
    private String appliedTierName;
    private LocalDateTime createdAt;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUser().getId())
                .userName(order.getUser().getName())
                .totalAmount(order.getTotalAmount())
                .description(order.getDescription())
                .appliedTierName(order.getAppliedTier() != null ? order.getAppliedTier().getName() : null)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
