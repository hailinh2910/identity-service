package com.linh.identity_service.dto.response;

import java.util.Set;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RoleResponse {

    private String name;
    private String description;
    private Set<PermissionResponse> permissions;
}
