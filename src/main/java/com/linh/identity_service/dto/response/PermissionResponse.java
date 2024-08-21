package com.linh.identity_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class PermissionResponse {

    private String name;
    private String description;

}
