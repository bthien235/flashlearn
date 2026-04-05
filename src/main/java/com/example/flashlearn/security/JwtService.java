package com.example.flashlearn.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private final SecretKey secretKey;
    private final long accessExpirationHours;
    private final long refreshExpirationDays;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-hours:24}") long accessExpirationHours,
            @Value("${app.jwt.refresh-expiration-days:30}") long refreshExpirationDays
    ) {
        String keyMaterial = secret;
        if (keyMaterial.length() < 32) {
            keyMaterial = (keyMaterial + "00000000000000000000000000000000").substring(0, 32);
        }
        this.secretKey = Keys.hmacShaKeyFor(keyMaterial.getBytes(StandardCharsets.UTF_8));
        this.accessExpirationHours = accessExpirationHours;
        this.refreshExpirationDays = refreshExpirationDays;
    }

    public String generateAccessToken(String userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(accessExpirationHours, ChronoUnit.HOURS);
        return Jwts.builder()
                .subject(userId)
                .claim("typ", "access")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    public String generateRefreshToken(String userId) {
        Instant now = Instant.now();
        Instant exp = now.plus(refreshExpirationDays, ChronoUnit.DAYS);
        return Jwts.builder()
                .subject(userId)
                .claim("typ", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(secretKey)
                .compact();
    }

    public String extractUserId(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            String typ = claims.get("typ", String.class);
            if (!expectedType.equals(typ)) {
                return null;
            }
            return claims.getSubject();
        } catch (Exception ex) {
            return null;
        }
    }

    public String extractAccessUserIdFromBearerToken(String header) {
        if (header == null || header.isBlank()) {
            return null;
        }
        String token = header.replace("Bearer ", "").trim();
        if (token.isBlank()) {
            return null;
        }
        return extractUserId(token, "access");
    }

    public String extractRefreshUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return extractUserId(token, "refresh");
    }
}
