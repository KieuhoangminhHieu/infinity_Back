package com.Infinity_CRM.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.Infinity_CRM.dto.request.RoleRequest;
import com.Infinity_CRM.dto.response.RoleResponse;
import com.Infinity_CRM.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    @Mapping(target = "permissions", ignore = true)
    Role toRole(RoleRequest request);

    RoleResponse toRoleResponse(Role role);
}

