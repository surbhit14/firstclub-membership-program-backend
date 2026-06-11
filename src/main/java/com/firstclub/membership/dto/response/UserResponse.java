package com.firstclub.membership.dto.response;

import com.firstclub.membership.entity.User;
import com.firstclub.membership.enums.Cohort;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class UserResponse {
    private Long id;
    private String name;
    private String email;
    private Cohort cohort;
    private LocalDateTime createdAt;

    public static UserResponse from(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .cohort(user.getCohort())
                .createdAt(user.getCreatedAt())
                .build();
    }
}
