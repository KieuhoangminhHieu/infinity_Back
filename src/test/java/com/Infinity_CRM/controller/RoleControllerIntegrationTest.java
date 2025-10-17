package com.Infinity_CRM.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.Infinity_CRM.dto.request.PermissionRequest;
import com.Infinity_CRM.dto.request.RoleRequest;
import com.Infinity_CRM.repository.PermissionRepository;
import com.Infinity_CRM.repository.RoleRepository;
import com.Infinity_CRM.service.PermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class RoleControllerIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Test
    void createRole_success() throws Exception {
        var permission = permissionService.create(PermissionRequest.builder()
                .name("MANAGE_ROLE")
                .description("Manage roles")
                .build());

        RoleRequest request = RoleRequest.builder()
                .name("ADMIN")
                .description("Admin role")
                .permissions(Set.of(permission.getName()))
                .build();

        String content = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("ADMIN"))
                .andExpect(jsonPath("$.result.permissions[0]").value("MANAGE_ROLE"));
    }

    @Test
    void getAllRoles_success() throws Exception {
        var permission = permissionService.create(PermissionRequest.builder()
                .name("VIEW_ROLE")
                .description("View roles")
                .build());

        RoleRequest request = RoleRequest.builder()
                .name("VIEWER")
                .description("Viewer role")
                .permissions(Set.of(permission.getName()))
                .build();

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result[0].name").value("VIEWER"));
    }

    @Test
    void deleteRole_success() throws Exception {
        var permission = permissionService.create(PermissionRequest.builder()
                .name("DELETE_ROLE")
                .description("Delete roles")
                .build());

        RoleRequest request = RoleRequest.builder()
                .name("MODERATOR")
                .description("Moderator role")
                .permissions(Set.of(permission.getName()))
                .build();

        mockMvc.perform(post("/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/roles/MODERATOR"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isEmpty());
    }
}