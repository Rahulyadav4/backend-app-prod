package com.taskmanager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.util.ReflectionTestUtils;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.FilterChain;

public class JwtAuthFilterTest {

    private static final String TEST_SECRET =
            "my-super-secret-key-for-testing-jwt-123456";

    // ============================================================
    // 1. VALID TOKEN
    // ============================================================

    @Test
    void testFilterWithValidToken() throws Exception {

        JwtAuthFilter filter = new JwtAuthFilter();

        // @Value("${JWT_SECRET}") is not loaded in a plain unit test.
        ReflectionTestUtils.setField(
                filter,
                "jwtSecret",
                TEST_SECRET
        );

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        SecretKey key =
                Keys.hmacShaKeyFor(
                        TEST_SECRET.getBytes(
                                StandardCharsets.UTF_8
                        )
                );

        String token =
                Jwts.builder()
                        .setSubject("admin")
                        .signWith(key)
                        .compact();

        request.setRequestURI("/tasks");

        request.addHeader(
                "Authorization",
                "Bearer " + token
        );

        filter.doFilter(
                request,
                response,
                filterChain
        );

        verify(filterChain).doFilter(
                request,
                response
        );

        assertEquals(
                200,
                response.getStatus()
        );
    }
    
    
    @Test
    void testFilterWithInvalidToken() throws Exception {

        JwtAuthFilter filter = new JwtAuthFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/tasks");
        request.addHeader("Authorization", "Bearer invalid-token");

        FilterChain filterChain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assertEquals(401, response.getStatus());

        verify(filterChain, never())
                .doFilter(request, response);
    }

    // ============================================================
    // 2. MISSING TOKEN
    // ============================================================

    @Test
    void testFilterWithMissingToken() throws Exception {

        JwtAuthFilter filter =
                new JwtAuthFilter();

        MockHttpServletRequest request =
                new MockHttpServletRequest();

        MockHttpServletResponse response =
                new MockHttpServletResponse();

        FilterChain filterChain =
                mock(FilterChain.class);

        request.setRequestURI("/tasks");

        filter.doFilter(
                request,
                response,
                filterChain
        );

        // Actual JwtAuthFilter behavior:
        // missing Authorization header -> continue chain

        verify(filterChain).doFilter(
                request,
                response
        );

        assertEquals(
                200,
                response.getStatus()
        );
    }
}
