package com.linh.identity_service.mapper;

import com.linh.identity_service.dto.request.PermissionRequest;
import com.linh.identity_service.dto.request.RoleRequest;
import com.linh.identity_service.dto.response.PermissionResponse;
import com.linh.identity_service.dto.response.RoleResponse;
import com.linh.identity_service.entity.Permission;
import com.linh.identity_service.entity.Role;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest roleRequest);




    @Mapping(target = "permissions", expression = "java(mapPermissions(role.getPermissions()))")
    RoleResponse toRoleResponse(Role role);

     private String mapPermissionToString(Permission permission) {
        return permission.getName();
    }

    // why using default? vi se co 1 class implement lai interface RoleMapper va xu ly
    // can mapPermissions de xu ly nen default giup cho class trien khai co the su dung  mapPermissions
    // ma khong can phai OVERRIDE. vi neu co 1 phuong thuc moi thi lop trien khai se can overrice -> error class implement

    default Set<String> mapPermissions(Set<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(this::mapPermissionToString)
                .collect(Collectors.toSet());
    }



}
