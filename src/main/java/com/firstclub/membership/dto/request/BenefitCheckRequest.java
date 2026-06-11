package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class BenefitCheckRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "orderAmount is required")
    @DecimalMin(value = "0.01", message = "orderAmount must be greater than 0")
    private BigDecimal orderAmount;
}
