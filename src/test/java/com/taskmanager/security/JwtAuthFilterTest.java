package com.taskmanager.security;

import jakarta.servlet.FilterChain;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import static org.mockito.Mockito.*;

public class JwtAuthFilterTest {

    @Test
    void testFilterWithValidToken() throws Exception {

        JwtAuthFilter filter = new JwtAuthFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        String token = JwtUtil.generateToken("admin");

        request.setRequestURI("/tasks");
        request.addHeader("Authorization", "Bearer " + token);

        FilterChain filterChain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    void testFilterWithMissingToken() throws Exception {

        JwtAuthFilter filter = new JwtAuthFilter();

        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();

        request.setRequestURI("/tasks");

        FilterChain filterChain = Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, filterChain);

        assert(response.getStatus() == 401);
    }

}