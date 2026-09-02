package com.taskmanager.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import jakarta.servlet.http.HttpServletRequest;

class LoginControllerTest {

    @Test
    void login_withValidCredentials_shouldReturnToken() {

        LoginController controller = new LoginController();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRemoteAddr())
                .thenReturn("10.0.0.1");

        Map<String, String> body = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        ResponseEntity<String> response =
                controller.login(body, request);

        assertEquals(
                HttpStatus.OK,
                response.getStatusCode()
        );

        assertNotNull(response.getBody());
    }

    @Test
    void login_withInvalidCredentials_shouldReturn401() {

        LoginController controller = new LoginController();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRemoteAddr())
                .thenReturn("10.0.0.2");

        Map<String, String> body = Map.of(
                "username", "wrong",
                "password", "wrong"
        );

        ResponseEntity<String> response =
                controller.login(body, request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertEquals(
                "Invalid credentials",
                response.getBody()
        );
    }

    @Test
    void login_withCorrectUsernameWrongPassword_shouldReturn401() {

        LoginController controller = new LoginController();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRemoteAddr())
                .thenReturn("10.0.0.3");

        Map<String, String> body = Map.of(
                "username", "admin",
                "password", "wrong"
        );

        ResponseEntity<String> response =
                controller.login(body, request);

        assertEquals(
                HttpStatus.UNAUTHORIZED,
                response.getStatusCode()
        );

        assertEquals(
                "Invalid credentials",
                response.getBody()
        );
    }

    @Test
    void login_afterTenAttempts_shouldReturn429() {

        LoginController controller = new LoginController();

        HttpServletRequest request =
                mock(HttpServletRequest.class);

        when(request.getRemoteAddr())
                .thenReturn("10.0.0.4");

        Map<String, String> body = Map.of(
                "username", "admin",
                "password", "admin123"
        );

        // Consume all 10 available tokens.
        for (int i = 0; i < 10; i++) {

            ResponseEntity<String> response =
                    controller.login(body, request);

            assertEquals(
                    HttpStatus.OK,
                    response.getStatusCode()
            );
        }

        // 11th request must be rate limited.
        ResponseEntity<String> response =
                controller.login(body, request);

        assertEquals(
                HttpStatus.TOO_MANY_REQUESTS,
                response.getStatusCode()
        );

        assertEquals(
                "Too many attempts. Try again in 1 minute.",
                response.getBody()
        );
    }
}