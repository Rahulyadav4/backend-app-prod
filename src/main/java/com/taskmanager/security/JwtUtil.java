package com.taskmanager.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import java.security.Key;
import java.util.Date;

public class JwtUtil {

    // FIX SDE-2: Read from env var — set in Minikube deployment YAML
    private static final String SECRET =
        System.getenv("JWT_SECRET") != null
            ? System.getenv("JWT_SECRET")
            : "local-dev-only-secret-key-32chars!!";

    private static final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public static String generateToken(String username) {
        return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + 3600000))
            .signWith(key)
            .compact();
    }

    public static String validateToken(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
}
