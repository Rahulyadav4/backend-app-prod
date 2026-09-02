package com.taskmanager.security;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import com.taskmanager.filter.RateLimitFilter;

@SpringJUnitConfig
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @MockBean
    private RateLimitFilter rateLimitFilter;

    @Autowired
    private SecurityFilterChain securityFilterChain;

    @Test
    void securityFilterChainShouldBeCreated() {
        assertNotNull(securityFilterChain);
    }
}