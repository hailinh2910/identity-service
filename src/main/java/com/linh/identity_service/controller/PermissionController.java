package com.linh.identity_service.controller;

import com.linh.identity_service.dto.request.ApiResponse;
import com.linh.identity_service.dto.request.PermissionRequest;
import com.linh.identity_service.dto.response.PermissionResponse;
import com.linh.identity_service.service.PermissionService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/permissions")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionService permissionService;

    @PostMapping()
    ApiResponse<PermissionResponse> createPermission( @RequestBody PermissionRequest permissionRequest) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionService.createPermission(permissionRequest))
                .build();
    }

    @GetMapping()
    ApiResponse<List<PermissionResponse>> listPermission(){

        return ApiResponse.<List<PermissionResponse>>builder()
                .result(permissionService.getAllPermissions())
                .build();
    }

    @DeleteMapping("/{permissionId}")
    ApiResponse<Void> deletePermission( @PathVariable String permissionId) {
          permissionService.deletePermission(permissionId);
        return  ApiResponse.<Void>builder()
                .build();
    }



}
