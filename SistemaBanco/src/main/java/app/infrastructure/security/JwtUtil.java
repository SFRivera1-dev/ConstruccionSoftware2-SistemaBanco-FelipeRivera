package app.infrastructure.security;

import app.domain.models.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    private final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long EXPIRATION_MS = 86400000; // 24 horas

    public String generateToken(User user) {
        Map<String, Object> claims = new HashMap<>();
        String role = user.getRole() != null
                ? user.getRole().name()
                : user.getCustomerRole().name();
        claims.put("role", role);
        claims.put("document", user.getDocument());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(SECRET_KEY)
                .compact();
    }

    public String extractEmail(String token) {
        return extractClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return (String) extractClaims(token).get("role");
    }

    public String extractDocument(String token) {
        return String.valueOf(extractClaims(token).get("document"));
    }

    public boolean isTokenValid(String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extractClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}