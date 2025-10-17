package com.Infinity_CRM.controller;

import com.Infinity_CRM.dto.request.TaskCreationRequest;
import com.Infinity_CRM.dto.request.TaskUpdateRequest;
import com.Infinity_CRM.dto.response.ApiResponse;
import com.Infinity_CRM.dto.response.TaskResponse;
import com.Infinity_CRM.service.TaskService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class TaskController {

    TaskService taskService;

    @PostMapping
    ApiResponse<TaskResponse> createTask(@RequestBody @Valid TaskCreationRequest request) {
        log.info("Creating task");
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.createTask(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<TaskResponse> updateTask(@PathVariable Long id, @RequestBody TaskUpdateRequest request) {
        return ApiResponse.<TaskResponse>builder()
                .result(taskService.updateTask(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<TaskResponse>> getAllTasks() {
        return ApiResponse.<List<TaskResponse>>builder()
                .result(taskService.getAllTasks())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
        return ApiResponse.<String>builder().result("Task has been deleted").build();
    }
}