package com.Infinity_CRM.mapper;

import com.Infinity_CRM.dto.request.TaskCreationRequest;
import com.Infinity_CRM.dto.request.TaskUpdateRequest;
import com.Infinity_CRM.dto.response.TaskResponse;
import com.Infinity_CRM.entity.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "Spring")
public interface TaskMapper {

    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "project", ignore = true)
    Task toTask(TaskCreationRequest request);

    TaskResponse toTaskResponse(Task task);

    @Mapping(target = "assignee", ignore = true)
    @Mapping(target = "project", ignore = true)
    void updateTask(@MappingTarget Task task, TaskUpdateRequest request);
}