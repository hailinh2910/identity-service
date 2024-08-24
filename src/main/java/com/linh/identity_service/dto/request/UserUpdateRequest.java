package com.linh.identity_service.dto.request;

import com.linh.identity_service.validator.DobConstraint;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class UserUpdateRequest {
    private String password;
    private String firstName;
    private String lastName;
    @DobConstraint(min = 18,message = "INVALID_AGE")
    private LocalDate dob;
    private List<String> roles;
}
