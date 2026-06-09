package com.reservex.auth.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Central Spring Security configuration.
 *
 * Route policy (mirrors the API spec Auth column):
 *
 *   PUBLIC  — /api/v1/auth/** (register, login, refresh, forgot/reset password, check-email)
 *           — GET /api/v1/events/**   (all event browsing)
 *           — GET /api/v1/inventory/shows/*//**availability  (listing page badge)
 *           — Swagger UI + OpenAPI docs
 *
 *   AUTH    — everything else requires a valid JWT
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter            jwtAuthFilter;
    private final CustomUserDetailsService userDetailsService;

    // ── filter chain ──────────────────────────────────────────────────────────

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // stateless REST API — no CSRF, no sessions
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .cors(c -> c.configurationSource(corsConfigurationSource()))

            .authorizeHttpRequests(auth -> auth

                // ── Swagger / OpenAPI ───────────────────────────────────────
                .requestMatchers(
                    "/swagger-ui/**",
                    "/swagger-ui.html",
                    "/v3/api-docs/**"
                ).permitAll()

                // ── Auth Service — PUBLIC endpoints ─────────────────────────
                .requestMatchers(
                    "/api/v1/auth/register",
                    "/api/v1/auth/login",
                    "/api/v1/auth/refresh",
                    "/api/v1/auth/forgot-password",
                    "/api/v1/auth/reset-password",
                    "/api/v1/auth/check-email"
                ).permitAll()

                // ── Event Service — all GET endpoints are PUBLIC ─────────────
                .requestMatchers(HttpMethod.GET, "/api/v1/events/**").permitAll()

                 // TEMPORARY FOR DEVELOPMENT
                .requestMatchers(HttpMethod.POST, "/api/v1/events/**").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/v1/events/**").permitAll()
                .requestMatchers(HttpMethod.DELETE, "/api/v1/events/**").permitAll()

                // ── Inventory — availability summary is PUBLIC ───────────────
                .requestMatchers(
                    HttpMethod.GET,
                    "/api/v1/inventory/shows/*/availability"
                ).permitAll()

                // ── Payment webhook is PUBLIC (HMAC verified in service) ─────
                .requestMatchers(HttpMethod.POST, "/api/v1/payments/webhook").permitAll()

                // ── everything else requires a valid JWT ─────────────────────
                .anyRequest().authenticated()
            )

            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // ── authentication provider (DAO + bcrypt) ────────────────────────────────

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);   // cost factor 12 as per spec
    }

    // ── CORS ──────────────────────────────────────────────────────────────────

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));   // tighten in production
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        

        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}