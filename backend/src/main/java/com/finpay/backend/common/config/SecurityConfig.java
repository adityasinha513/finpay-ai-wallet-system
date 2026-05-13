package com.finpay.backend.common.config;

import com.finpay.backend.common.security.JwtAuthenticationEntryPoint;
import com.finpay.backend.common.security.JwtAuthenticationFilter;
import com.finpay.backend.common.security.RestAccessDeniedHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter
            jwtAuthenticationFilter;

    private final JwtAuthenticationEntryPoint
            jwtAuthenticationEntryPoint;

    private final RestAccessDeniedHandler
            restAccessDeniedHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {

        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config
    ) throws Exception {

        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(
                                jwtAuthenticationEntryPoint
                        )
                        .accessDeniedHandler(
                                restAccessDeniedHandler
                        )
                )

                        .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                        .requestMatchers(
                                "/api/v1/auth/**",

                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",

                                "/health",

                                "/actuator/health",
                                "/actuator/health/**"
                        ).permitAll()

                        .requestMatchers(
                                "/api/v1/admin/**"
                        ).hasRole("ADMIN")

                        .anyRequest().authenticated()
                );

        http.addFilterBefore(
                jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class
        );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(
                Arrays.asList(
                        "http://localhost:3000",
                        "http://127.0.0.1:3000"
                )
        );
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Cache-Control", "Content-Type", "Accept")
        );
        configuration.setExposedHeaders(
                Arrays.asList("Authorization")
        );
        configuration.setAllowCredentials(false);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }
}