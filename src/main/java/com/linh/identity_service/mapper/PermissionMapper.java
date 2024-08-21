package com.linh.identity_service.mapper;

import com.linh.identity_service.dto.request.PermissionRequest;
import com.linh.identity_service.dto.response.PermissionResponse;
import com.linh.identity_service.entity.Permission;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest permissionRequest);
    PermissionResponse toPermissionResponse(Permission permission);
}
