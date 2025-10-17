package com.Infinity_CRM.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;

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
import com.Infinity_CRM.dto.request.RoleRequest;
import com.Infinity_CRM.dto.response.RoleResponse;
import com.Infinity_CRM.repository.PermissionRepository;
import com.Infinity_CRM.repository.RoleRepository;
import com.Infinity_CRM.service.PermissionService;

@SpringBootTest
@Testcontainers
class RoleServiceIntegrationTest {

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
    RoleService roleService;

    @Autowired
    PermissionService permissionService;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    PermissionRepository permissionRepository;

    @BeforeEach
    void setup() {
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    @Test
    void createRole_success() {
        var permission = permissionService.create(PermissionRequest.builder()
                .name("MANAGE_USER")
                .description("Manage user accounts")
                .build());

        RoleRequest request = RoleRequest.builder()
                .name("ADMIN")
                .description("Administrator role")
                .permissions(Set.of(permission.getName()))
                .build();

        RoleResponse response = roleService.create(request);

        assertNotNull(response);
        assertEquals("ADMIN", response.getName());
        assertTrue(response.getPermissions().contains("MANAGE_USER"));
    }

    @Test
    void getAllRoles_success() {
        var p1 = permissionService.create(PermissionRequest.builder()
                .name("VIEW_REPORT")
                .description("View reports")
                .build());

        var p2 = permissionService.create(PermissionRequest.builder()
                .name("EXPORT_DATA")
                .description("Export data")
                .build());

        roleService.create(RoleRequest.builder()
                .name("REPORTER")
                .description("Report viewer")
                .permissions(Set.of(p1.getName(), p2.getName()))
                .build());

        List<RoleResponse> roles = roleService.getAll();

        assertEquals(1, roles.size());
        assertEquals("REPORTER", roles.get(0).getName());
        assertTrue(roles.get(0).getPermissions().contains("VIEW_REPORT"));
        assertTrue(roles.get(0).getPermissions().contains("EXPORT_DATA"));
    }

    @Test
    void deleteRole_success() {
        var permission = permissionService.create(PermissionRequest.builder()
                .name("DELETE_USER")
                .description("Delete user accounts")
                .build());

        RoleResponse created = roleService.create(RoleRequest.builder()
                .name("MODERATOR")
                .description("Moderator role")
                .permissions(Set.of(permission.getName()))
                .build());

        roleService.delete(created.getName());

        List<RoleResponse> roles = roleService.getAll();
        assertTrue(roles.stream().noneMatch(r -> r.getName().equals("MODERATOR")));
    }
}