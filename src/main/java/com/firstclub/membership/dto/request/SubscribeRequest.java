package com.firstclub.membership.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SubscribeRequest {

    @NotNull(message = "userId is required")
    private Long userId;

    @NotNull(message = "planId is required")
    private Long planId;
}
