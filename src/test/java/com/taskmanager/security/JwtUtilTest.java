package com.taskmanager.security;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class JwtUtilTest {

    @Test
    void testGenerateAndValidateToken() {

        String token = JwtUtil.generateToken("admin");

        String username = JwtUtil.validateToken(token);

        assertEquals("admin", username);
    }
}