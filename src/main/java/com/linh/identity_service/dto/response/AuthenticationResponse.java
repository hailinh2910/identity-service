package com.linh.identity_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class AuthenticationResponse {
    private String token;
    private boolean authenticated;
}
