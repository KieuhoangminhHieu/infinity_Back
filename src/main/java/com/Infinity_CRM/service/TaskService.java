package com.Infinity_CRM.service;

import com.Infinity_CRM.dto.request.TaskCreationRequest;
import com.Infinity_CRM.dto.request.TaskUpdateRequest;
import com.Infinity_CRM.dto.response.TaskResponse;
import com.Infinity_CRM.entity.Task;
import com.Infinity_CRM.entity.User;
import com.Infinity_CRM.entity.Project;
import com.Infinity_CRM.enums.TaskStatus;
import com.Infinity_CRM.exception.AppException;
import com.Infinity_CRM.exception.ErrorCode;
import com.Infinity_CRM.mapper.TaskMapper;
import com.Infinity_CRM.repository.TaskRepository;
import com.Infinity_CRM.repository.UserRepository;
import com.Infinity_CRM.repository.ProjectRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TaskService {

    TaskRepository taskRepository;
    UserRepository userRepository;
    ProjectRepository projectRepository;
    TaskMapper taskMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse createTask(TaskCreationRequest request) {
        log.info("Creating task for assignee: {}", request.getAssigneeId());
        Task task = taskMapper.toTask(request);

        User assignee = userRepository.findById(request.getAssigneeId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Project project = projectRepository.findById(request.getProjectId())
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        task.setAssignee(assignee);
        task.setProject(project);
        task.setStatus(TaskStatus.valueOf(request.getStatus()));

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public TaskResponse updateTask(Long id, TaskUpdateRequest request) {
        log.info("Updating task ID: {}", id);
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.TASK_NOT_FOUND));

        taskMapper.updateTask(task, request);

        if (request.getAssigneeId() != null) {
            User assignee = userRepository.findById(request.getAssigneeId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            task.setAssignee(assignee);
        }

        if (request.getProjectId() != null) {
            Project project = projectRepository.findById(request.getProjectId())
                    .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));
            task.setProject(project);
        }

        if (request.getStatus() != null) {
            task.setStatus(TaskStatus.valueOf(request.getStatus()));
        }

        return taskMapper.toTaskResponse(taskRepository.save(task));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<TaskResponse> getAllTasks() {
        log.info("Fetching all tasks");
        return taskRepository.findAll().stream()
                .map(taskMapper::toTaskResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteTask(Long id) {
        log.info("Deleting task ID: {}", id);
        taskRepository.deleteById(id);
    }
}