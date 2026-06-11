package com.firstclub.membership.dto.request;

import com.firstclub.membership.enums.Cohort;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    // Typed enum — invalid cohort names are rejected at deserialization time
    private Cohort cohort;
}
