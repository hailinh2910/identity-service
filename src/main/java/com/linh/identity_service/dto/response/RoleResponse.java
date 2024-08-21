package com.linh.identity_service.dto.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class RoleResponse {

    private String name;
    private String description;
    private Set<String> permissions;
}
