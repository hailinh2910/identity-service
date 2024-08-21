package com.linh.identity_service.controller;

import com.linh.identity_service.dto.request.ApiResponse;
import com.linh.identity_service.dto.request.PermissionRequest;
import com.linh.identity_service.dto.request.RoleRequest;
import com.linh.identity_service.dto.response.PermissionResponse;
import com.linh.identity_service.dto.response.RoleResponse;
import com.linh.identity_service.service.PermissionService;
import com.linh.identity_service.service.RoleService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleService roleService;

    @PostMapping()
    ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest roleRequest) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleService.createRole(roleRequest))
                .build();
    }

    @GetMapping()
    ApiResponse<List<RoleResponse>> listRole(){

        return ApiResponse.<List<RoleResponse>>builder()
                .result(roleService.getRoles())
                .build();
    }

    @DeleteMapping("/{roleId}")
    ApiResponse<Void> deleteRole( @PathVariable String roleId) {
        roleService.deleteRole(roleId);
        return  ApiResponse.<Void>builder()
                .build();
    }



}
