package com.Infinity_CRM.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.Infinity_CRM.dto.request.PermissionRequest;
import com.Infinity_CRM.dto.response.PermissionResponse;
import com.Infinity_CRM.repository.PermissionRepository;

@SpringBootTest
@Testcontainers
class PermissionServiceIntegrationTest {

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
    PermissionService permissionService;

    @Autowired
    PermissionRepository permissionRepository;

    @BeforeEach
    void setup() {
        permissionRepository.deleteAll();
    }

    @Test
    void createPermission_success() {
        PermissionRequest request = PermissionRequest.builder()
                .name("VIEW_USER")
                .description("Permission to view user")
                .build();

        PermissionResponse response = permissionService.create(request);

        assertNotNull(response);
        assertEquals("VIEW_USER", response.getName());
    }

    @Test
    void getAllPermissions_success() {
        permissionService.create(PermissionRequest.builder()
                .name("CREATE_USER")
                .description("Permission to create user")
                .build());

        permissionService.create(PermissionRequest.builder()
                .name("DELETE_USER")
                .description("Permission to delete user")
                .build());

        List<PermissionResponse> all = permissionService.getAll();

        assertEquals(2, all.size());
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("CREATE_USER")));
        assertTrue(all.stream().anyMatch(p -> p.getName().equals("DELETE_USER")));
    }

    @Test
    void deletePermission_success() {
        PermissionResponse created = permissionService.create(PermissionRequest.builder()
                .name("UPDATE_USER")
                .description("Permission to update user")
                .build());

        permissionService.delete(created.getName());

        List<PermissionResponse> all = permissionService.getAll();
        assertTrue(all.stream().noneMatch(p -> p.getName().equals("UPDATE_USER")));
    }
}