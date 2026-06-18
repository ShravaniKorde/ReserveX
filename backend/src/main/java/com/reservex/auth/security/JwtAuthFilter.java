package com.reservex.auth.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * Intercepts every HTTP request once.
 *
 * If a valid Bearer token is present:
 *   1. Parses the JWT.
 *   2. Builds an Authentication object (user_id as principal, role as authority).
 *   3. Puts it in the SecurityContext so @PreAuthorize and other checks work.
 *
 * If the token is missing or invalid, the request continues unauthenticated —
 * Spring Security will block it at the route level if the route requires AUTH.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest  request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain         filterChain
    ) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7); // strip "Bearer "

        if (!jwtUtil.isTokenValid(token)) {
            log.debug("Invalid or expired JWT on request to {}", request.getRequestURI());
            filterChain.doFilter(request, response);
            return;
        }

        // Token is valid — extract claims and populate SecurityContext
        Claims claims  = jwtUtil.parseToken(token);
        String userId  = jwtUtil.extractUserId(claims);
        String role    = jwtUtil.extractRole(claims);

        var authorities = List.of(new SimpleGrantedAuthority(role));

        var authentication = new UsernamePasswordAuthenticationToken(
                userId,     // principal — available via SecurityContextHolder anywhere
                null,       // credentials — not needed after auth
                authorities
        );
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}