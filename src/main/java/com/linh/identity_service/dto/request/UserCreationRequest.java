package com.linh.identity_service.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
@Getter
@Setter
public class UserCreationRequest {

    @Size(min = 3, message = "USERNAME_ERROR")
    private String username;
    @Size(min = 8, message = "PASSWORD_ERROR")
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate dob;
}
