package com.Infinity_CRM.controller;

import com.Infinity_CRM.dto.request.ProjectCreationRequest;
import com.Infinity_CRM.dto.request.ProjectUpdateRequest;
import com.Infinity_CRM.dto.response.ApiResponse;
import com.Infinity_CRM.dto.response.ProjectResponse;
import com.Infinity_CRM.service.ProjectService;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProjectController {

    ProjectService projectService;

    @PostMapping
    ApiResponse<ProjectResponse> createProject(@RequestBody @Valid ProjectCreationRequest request) {
        log.info("Creating project: {}", request.getName());
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.createProject(request))
                .build();
    }

    @PutMapping("/{id}")
    ApiResponse<ProjectResponse> updateProject(@PathVariable Long id, @RequestBody ProjectUpdateRequest request) {
        log.info("Updating project ID: {}", id);
        return ApiResponse.<ProjectResponse>builder()
                .result(projectService.updateProject(id, request))
                .build();
    }

    @GetMapping
    ApiResponse<List<ProjectResponse>> getAllProjects() {
        log.info("Fetching all projects");
        return ApiResponse.<List<ProjectResponse>>builder()
                .result(projectService.getAllProjects())
                .build();
    }

    @DeleteMapping("/{id}")
    ApiResponse<String> deleteProject(@PathVariable Long id) {
        log.info("Deleting project ID: {}", id);
        projectService.deleteProject(id);
        return ApiResponse.<String>builder()
                .result("Project has been deleted")
                .build();
    }
}