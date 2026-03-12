package com.taskmanager.security;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter
{

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain)
	        throws ServletException, IOException, java.io.IOException {

	    String path = request.getRequestURI();

	    // 🔓 Allow login without token
	    if (path.startsWith("/auth")) {
	        filterChain.doFilter(request, response);
	        return;
	    }

	    String authHeader = request.getHeader("Authorization");

	    if (authHeader != null && authHeader.startsWith("Bearer ")) {
	        String token = authHeader.substring(7);
	        try {
	            String username = JwtUtil.validateToken(token);
	            request.setAttribute("username", username);
	        } catch (Exception e) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.getWriter().write("Invalid or expired token");
	            return;
	        }
	    } else {
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.getWriter().write("Missing token");
	        return;
	    }
	    filterChain.doFilter(request, response);
	}
}