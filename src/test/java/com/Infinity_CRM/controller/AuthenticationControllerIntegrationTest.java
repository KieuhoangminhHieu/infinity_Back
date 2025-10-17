package com.Infinity_CRM.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
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

import com.Infinity_CRM.dto.request.AuthenticationRequest;
import com.Infinity_CRM.dto.request.IntrospectRequest;
import com.Infinity_CRM.dto.request.LogoutRequest;
import com.Infinity_CRM.dto.request.RefreshRequest;
import com.Infinity_CRM.dto.request.UserCreationRequest;
import com.Infinity_CRM.entity.Role;
import com.Infinity_CRM.entity.User;
import com.Infinity_CRM.mapper.UserMapper;
import com.Infinity_CRM.repository.RoleRepository;
import com.Infinity_CRM.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
class AuthenticationControllerIntegrationTest {

    @Container
    static final MySQLContainer<?> MYSQL = new MySQLContainer<>("mysql:8.0.36");

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", MYSQL::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL::getUsername);
        registry.add("spring.datasource.password", MYSQL::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "com.mysql.cj.jdbc.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "update");
        registry.add("jwt.signerKey", () -> "bXlTdXBlclNlY3JldEtleUhlcmVXaXRoNjRieXRlc1RvU2lnblRva2VuU3VjY2Vzcw==");
        registry.add("jwt.valid-duration", () -> "3600");
        registry.add("jwt.refreshable-duration", () -> "86400");
    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserMapper userMapper;

    ObjectMapper objectMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = Role.builder()
                .name("USER")
                .description("Default user role")
                .build();
        roleRepository.save(role);

        UserCreationRequest request = UserCreationRequest.builder()
                .firstName("Kiều")
                .lastName("Hoàng")
                .username("kieutest")
                .password("12345678")
                .dob(LocalDate.of(2000, 1, 1))
                .email("kieutest@gmail.com")
                .phoneNumber("0123456789")
                .address("Hà Nội")
                .build();

        User user = userMapper.toUser(request);
        user.setRoles(Set.of(role));
        userRepository.save(user);

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Test
    void authenticate_success() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("kieutest", "12345678");

        mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andExpect(jsonPath("$.result.token").isNotEmpty());
    }

    @Test
    void introspect_validToken_success() throws Exception {
        String token = getToken();

        IntrospectRequest request = IntrospectRequest.builder().token(token).build();

        mockMvc.perform(post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.valid").value(true));
    }

    @Test
    void refreshToken_success() throws Exception {
        String token = getToken();

        RefreshRequest request = RefreshRequest.builder().token(token).build();

        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.authenticated").value(true))
                .andExpect(jsonPath("$.result.token").isNotEmpty());
    }

    @Test
    void logout_success_then_token_invalid() throws Exception {
        String token = getToken();

        LogoutRequest logoutRequest = LogoutRequest.builder().token(token).build();

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(logoutRequest)))
                .andExpect(status().isOk());

        IntrospectRequest introspectRequest = IntrospectRequest.builder().token(token).build();

        mockMvc.perform(post("/auth/introspect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(introspectRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.valid").value(false));
    }

    private String getToken() throws Exception {
        AuthenticationRequest request = new AuthenticationRequest("kieutest", "12345678");

        var result = mockMvc.perform(post("/auth/token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        return objectMapper.readTree(json).get("result").get("token").asText();
    }
}