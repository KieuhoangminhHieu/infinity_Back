package com.Infinity_CRM.service;

import com.Infinity_CRM.dto.request.ProjectCreationRequest;
import com.Infinity_CRM.dto.request.ProjectUpdateRequest;
import com.Infinity_CRM.dto.response.ProjectResponse;
import com.Infinity_CRM.entity.Project;
import com.Infinity_CRM.entity.User;
import com.Infinity_CRM.exception.AppException;
import com.Infinity_CRM.exception.ErrorCode;
import com.Infinity_CRM.mapper.ProjectMapper;
import com.Infinity_CRM.repository.ProjectRepository;
import com.Infinity_CRM.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class ProjectService {

    ProjectRepository projectRepository;
    UserRepository userRepository;
    ProjectMapper projectMapper;

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse createProject(ProjectCreationRequest request) {
        log.info("Creating project: {}", request.getName());
        Project project = projectMapper.toProject(request);

        User owner = userRepository.findById(request.getOwnerId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
        Set<User> members = new HashSet<>();
        for (String memberId : request.getMemberIds()) {
            User member = userRepository.findById(memberId)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            members.add(member);
        }

        project.setOwner(owner);
        project.setMembers(members);

        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public ProjectResponse updateProject(Long id, ProjectUpdateRequest request) {
        log.info("Updating project ID: {}", id);
        Project project = projectRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROJECT_NOT_FOUND));

        projectMapper.updateProject(project, request);

        if (request.getOwnerId() != null) {
            User owner = userRepository.findById(request.getOwnerId())
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            project.setOwner(owner);
        }

        if (request.getMemberIds() != null) {
            Set<User> members = new HashSet<>();
            for (String memberId : request.getMemberIds()) {
                User member = userRepository.findById(memberId)
                        .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
                members.add(member);
            }
            project.setMembers(members);
        }

        return projectMapper.toProjectResponse(projectRepository.save(project));
    }

    @Transactional
    @PreAuthorize("hasAnyRole('ADMIN', 'USER')")
    public List<ProjectResponse> getAllProjects() {
        log.info("Fetching all projects");
        return projectRepository.findAll().stream()
                .map(projectMapper::toProjectResponse)
                .toList();
    }

    @Transactional
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProject(Long id) {
        log.info("Deleting project ID: {}", id);
        projectRepository.deleteById(id);
    }
}