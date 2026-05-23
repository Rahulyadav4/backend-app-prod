package com.taskmanager.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoginControllerTest {

    private final LoginController loginController = new LoginController();

    @Test
    public void testValidLogin() {
        Map<String, String> body = Map.of(
            "username", "admin",
            "password", "admin123"
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        ResponseEntity<String> response = loginController.login(body, request);

        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    public void testInvalidLogin() {
        Map<String, String> body = Map.of(
            "username", "admin",
            "password", "wrongpassword"
        );
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRemoteAddr("127.0.0.1");

        ResponseEntity<String> response = loginController.login(body, request);

        assertEquals(401, response.getStatusCode().value());
    }
}
