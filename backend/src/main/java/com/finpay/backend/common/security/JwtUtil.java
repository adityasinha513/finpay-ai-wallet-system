package com.finpay.backend.common.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Component
@Getter
@Slf4j
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    public String generateToken(String email) {

        Date now = new Date();

        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    /**
     * Parses and validates the token in one step. Invalid/expired/malformed tokens yield empty.
     * Used by the JWT filter to avoid exceptions propagating as HTTP 500.
     */
    public Optional<String> validateTokenAndGetSubject(String token) {

        if (token == null || token.isBlank()) {
            return Optional.empty();
        }

        try {

            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            return Optional.ofNullable(claims.getSubject())
                    .filter(s -> !s.isBlank());

        } catch (JwtException | IllegalArgumentException ex) {

            log.debug("JWT validation failed: {}", ex.getMessage());
            return Optional.empty();
        }
    }

    /**
     * @deprecated Prefer {@link #validateTokenAndGetSubject(String)} for safe parsing.
     */
    @Deprecated
    public String extractEmail(String token) {

        return validateTokenAndGetSubject(token)
                .orElseThrow(() ->
                        new JwtException("Invalid JWT")
                );
    }

    /**
     * @deprecated Prefer {@link #validateTokenAndGetSubject(String)} to parse once.
     */
    @Deprecated
    public boolean isTokenValid(String token) {

        return validateTokenAndGetSubject(token).isPresent();
    }
}
