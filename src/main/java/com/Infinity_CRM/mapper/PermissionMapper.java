package com.Infinity_CRM.mapper;

import org.mapstruct.Mapper;

import com.Infinity_CRM.dto.request.PermissionRequest;
import com.Infinity_CRM.dto.response.PermissionResponse;
import com.Infinity_CRM.entity.Permission;

@Mapper(componentModel = "Spring")
public interface PermissionMapper {
    Permission toPermission(PermissionRequest request);

    PermissionResponse toPermissionResponse(Permission permission);
}
