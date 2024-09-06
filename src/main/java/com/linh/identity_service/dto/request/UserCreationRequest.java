package com.linh.identity_service.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Size;

import com.linh.identity_service.validator.DobConstraint;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserCreationRequest {

    @Size(min = 4, message = "USERNAME_ERROR") // config in enum
    private String username;

    @Size(min = 8, message = "PASSWORD_ERROR")
    private String password;

    private String firstName;
    private String lastName;

    @DobConstraint(min = 18, message = "INVALID_AGE")
    private LocalDate dob;
}
