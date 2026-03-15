package com.taskmanager.controller;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class LoginControllerTest {

    @Test
    void testValidLogin() {
        LoginController controller = new LoginController();

        Map<String, String> body = new HashMap<>();
        body.put("username", "admin");
        body.put("password", "admin123");

        String token = controller.login(body);

        assertNotNull(token);
    }

    @Test
    void testInvalidLogin() {
        LoginController controller = new LoginController();

        Map<String, String> body = new HashMap<>();
        body.put("username", "wrong");
        body.put("password", "wrong");

        String result = controller.login(body);

        assertEquals("Invalid Credentials", result);
    }
}