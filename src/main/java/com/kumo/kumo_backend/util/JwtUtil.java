package com.kumo.kumo_backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();

        // 🔥 AGREGAR ID Y ROL AL TOKEN
        if (userDetails instanceof com.kumo.kumo_backend.model.User) {
            com.kumo.kumo_backend.model.User user = (com.kumo.kumo_backend.model.User) userDetails;
            claims.put("id", user.getId());
            claims.put("rol", user.getRol());
            claims.put("email", user.getEmail());
            System.out.println(" Generando token para: " + user.getEmail() + " (ID: " + user.getId() + ")");
        } else {
            System.err.println(" UserDetails NO es una instancia de User de Kumo");
            System.err.println("   Clase: " + userDetails.getClass().getName());
        }

        return createToken(claims, userDetails.getUsername());
    }
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // 🔥 NUEVO: Extraer ID del usuario del token
    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object userId = claims.get("id");
            if (userId != null) {
                return Long.valueOf(userId.toString());
            }
            return null;
        } catch (Exception e) {
            System.err.println("❌ Error al extraer userId: " + e.getMessage());
            return null;
        }
    }

    // 🔥 NUEVO: Extraer rol del usuario del token
    public String extractRol(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Object rol = claims.get("rol");
            return rol != null ? rol.toString() : "USER";
        } catch (Exception e) {
            System.err.println("❌ Error al extraer rol: " + e.getMessage());
            return "USER";
        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}