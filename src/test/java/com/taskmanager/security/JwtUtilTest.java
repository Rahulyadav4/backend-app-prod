package com.taskmanager.security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class JwtUtilTest {

    @Test
    void generateTokenShouldCreateValidToken() {

        String username = "rahul";

        String token = JwtUtil.generateToken(username);

        assertNotNull(token);
    }

    @Test
    void validateTokenShouldReturnUsername() {

        String username = "rahul";

        String token = JwtUtil.generateToken(username);

        String result = JwtUtil.validateToken(token);

        assertEquals(username, result);
    }

    @Test
    void validateTokenShouldRejectInvalidToken() {

        String invalidToken = "invalid.token.value";

        assertThrows(Exception.class, () -> {
            JwtUtil.validateToken(invalidToken);
        });
    }
}