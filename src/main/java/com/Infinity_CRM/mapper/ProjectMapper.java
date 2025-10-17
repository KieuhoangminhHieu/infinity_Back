package com.Infinity_CRM.mapper;

import com.Infinity_CRM.dto.request.ProjectCreationRequest;
import com.Infinity_CRM.dto.request.ProjectUpdateRequest;
import com.Infinity_CRM.dto.response.ProjectResponse;
import com.Infinity_CRM.entity.Project;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface ProjectMapper {

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    Project toProject(ProjectCreationRequest request);

    ProjectResponse toProjectResponse(Project project);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "members", ignore = true)
    @Mapping(target = "tasks", ignore = true)
    void updateProject(@MappingTarget Project project, ProjectUpdateRequest request);
}