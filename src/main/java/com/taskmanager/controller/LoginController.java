package com.taskmanager.controller;

import com.taskmanager.security.JwtUtil;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class LoginController {

    @PostMapping("/login")//get typ in postman//
    public String login(@RequestBody Map<String, String> body) {

        String user = body.get("username");
        String pass = body.get("password");

        // Hardcoded credentials for POC version
        if ("admin".equals(user) && "admin123".equals(pass)) {
            return JwtUtil.generateToken(user);
        }

        return "Invalid Credentials";
    }
}