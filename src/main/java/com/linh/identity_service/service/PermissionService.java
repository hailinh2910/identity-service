package com.linh.identity_service.service;

import com.linh.identity_service.dto.request.PermissionRequest;
import com.linh.identity_service.dto.response.PermissionResponse;
import com.linh.identity_service.entity.Permission;
import com.linh.identity_service.mapper.PermissionMapper;
import com.linh.identity_service.repository.PermissionRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class PermissionService {
    PermissionRepository permissionRepository;
    PermissionMapper permissionMapper;

    public  PermissionResponse createPermission (PermissionRequest permissionRequest) {

        Permission permission = permissionMapper.toPermission(permissionRequest);
        permissionRepository.save(permission);
        return permissionMapper.toPermissionResponse(permission);

    }
    public List<PermissionResponse> getAllPermissions() {
        List<Permission> permissions = permissionRepository.findAll();
        return  permissions.stream().map(permissionMapper::toPermissionResponse).toList();

    }

    public void deletePermission (String permissionId) {
        permissionRepository.deleteById(permissionId);
    }

}
