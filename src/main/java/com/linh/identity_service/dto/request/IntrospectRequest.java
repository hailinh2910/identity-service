package com.linh.identity_service.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class IntrospectRequest {
    private String token;
}
