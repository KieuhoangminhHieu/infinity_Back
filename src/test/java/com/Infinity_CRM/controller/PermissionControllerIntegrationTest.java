package com.Infinity_CRM.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import com.Infinity_CRM.repository.PermissionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class PermissionControllerIntegrationTest {

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
    PermissionRepository permissionRepository;

    @Autowired
    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        permissionRepository.deleteAll();
    }

    @Test
    void createPermission_success() throws Exception {
        PermissionRequest request = PermissionRequest.builder()
                .name("CREATE_USER")
                .description("Permission to create user")
                .build();

        mockMvc.perform(post("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.name").value("CREATE_USER"))
                .andExpect(jsonPath("$.result.description").value("Permission to create user"));
    }

    @Test
    void getAllPermissions_success() throws Exception {
        List<PermissionRequest> requests = List.of(
                PermissionRequest.builder().name("VIEW_USER").description("View user").build(),
                PermissionRequest.builder().name("DELETE_USER").description("Delete user").build()
        );

        for (PermissionRequest request : requests) {
            mockMvc.perform(post("/permissions")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(get("/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.length()").value(2));
    }

    @Test
    void deletePermission_success() throws Exception {
        PermissionRequest request = PermissionRequest.builder()
                .name("UPDATE_USER")
                .description("Update user")
                .build();

        mockMvc.perform(post("/permissions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/permissions/UPDATE_USER"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").isEmpty());
    }
}