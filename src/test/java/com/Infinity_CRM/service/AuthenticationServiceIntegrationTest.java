package com.Infinity_CRM.service;

import static org.junit.jupiter.api.Assertions.*;

import java.text.ParseException;
import java.time.LocalDate;
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

import com.Infinity_CRM.dto.request.AuthenticationRequest;
import com.Infinity_CRM.dto.request.IntrospectRequest;
import com.Infinity_CRM.dto.request.LogoutRequest;
import com.Infinity_CRM.dto.request.RefreshRequest;
import com.Infinity_CRM.dto.response.AuthenticationResponse;
import com.Infinity_CRM.dto.response.IntrospectResponse;
import com.Infinity_CRM.entity.Role;
import com.Infinity_CRM.entity.User;
import com.Infinity_CRM.mapper.UserMapper;
import com.Infinity_CRM.repository.RoleRepository;
import com.Infinity_CRM.repository.UserRepository;

@SpringBootTest
@Testcontainers
class AuthenticationServiceIntegrationTest {

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
    AuthenticationService authenticationService;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    UserMapper userMapper;

    @BeforeEach
    void setup() {
        userRepository.deleteAll();
        roleRepository.deleteAll();

        Role role = Role.builder()
                .name("USER")
                .description("Default user role")
                .build();
        roleRepository.save(role);

        User user = User.builder()
                .firstName("Kiều")
                .lastName("Hoàng")
                .username("kieutest")
                .password(new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder().encode("12345678"))
                .email("kieutest@gmail.com")
                .dob(LocalDate.of(2000, 1, 1))
                .roles(Set.of(role))
                .build();
        userRepository.save(user);
    }
    @Test
    void authenticate_success() {
        AuthenticationRequest request = new AuthenticationRequest("kieutest", "12345678");

        AuthenticationResponse response = authenticationService.authenticate(request);

        assertTrue(response.isAuthenticated());
        assertNotNull(response.getToken());
    }
    @Test
    void introspect_validToken_success() throws Exception {
        String token = authenticationService.authenticate(
                new AuthenticationRequest("kieutest", "12345678")
        ).getToken();

        IntrospectResponse response = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build()
        );

        assertTrue(response.isValid());
    }
    @Test
    void refreshToken_success() throws Exception {
        String token = authenticationService.authenticate(
                new AuthenticationRequest("kieutest", "12345678")
        ).getToken();

        AuthenticationResponse refreshed = authenticationService.refreshToken(
                RefreshRequest.builder().token(token).build()
        );

        assertTrue(refreshed.isAuthenticated());
        assertNotEquals(token, refreshed.getToken());
    }
    @Test
    void logout_then_token_invalid() throws Exception {
        String token = authenticationService.authenticate(
                new AuthenticationRequest("kieutest", "12345678")
        ).getToken();

        authenticationService.logout(LogoutRequest.builder().token(token).build());

        IntrospectResponse response = authenticationService.introspect(
                IntrospectRequest.builder().token(token).build()
        );

        assertFalse(response.isValid());
    }




}