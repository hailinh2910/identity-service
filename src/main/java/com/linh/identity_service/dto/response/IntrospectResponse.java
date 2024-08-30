package com.linh.identity_service.dto.response;

import lombok.*;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class IntrospectResponse {
    private boolean valid;
}
