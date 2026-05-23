package com.taskmanager.controller;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import com.taskmanager.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/auth")
public class LoginController {

    // FIX SDE-2: Read from env vars set in Minikube deployment
    private final String appUsername =
        System.getenv("APP_USERNAME") != null ? System.getenv("APP_USERNAME") : "admin";
    private final String appPassword =
        System.getenv("APP_PASSWORD") != null ? System.getenv("APP_PASSWORD") : "admin123";

    // FIX SDE-2: Rate limit — 10 attempts per minute per IP
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();

    private Bucket getBucket(String ip) {
        return buckets.computeIfAbsent(ip, k ->
            Bucket.builder()
                .addLimit(Bandwidth.classic(10,
                    Refill.intervally(10, Duration.ofMinutes(1))))
                .build());
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(
            @RequestBody Map<String, String> body,
            HttpServletRequest request) {

        // FIX: Rate limiting check
        if (!getBucket(request.getRemoteAddr()).tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Too many attempts. Try again in 1 minute.");
        }

        String user = body.get("username");
        String pass = body.get("password");

        if (appUsername.equals(user) && appPassword.equals(pass)) {
            return ResponseEntity.ok(JwtUtil.generateToken(user));
        }

        // FIX: Return 401 not 200 on bad credentials
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body("Invalid credentials");
    }
}
