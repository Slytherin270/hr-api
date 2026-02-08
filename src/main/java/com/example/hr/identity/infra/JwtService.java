package com.example.hr.identity.infra;

import com.example.hr.identity.domain.Role;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtService {
    private final String issuer;
    private final byte[] secret;
    private final long ttlSeconds;

    public JwtService(@Value("${app.security.jwt.issuer}") String issuer,
                      @Value("${app.security.jwt.secret}") String secret,
                      @Value("${app.security.jwt.ttl}") long ttlSeconds) {
        this.issuer = issuer;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.ttlSeconds = ttlSeconds;
    }

    public String generateToken(UUID employeeId, String email, Role role) {
        Instant now = Instant.now();
        return Jwts.builder()
                .setSubject(employeeId.toString())
                .setIssuer(issuer)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plusSeconds(ttlSeconds)))
                .claim("email", email)
                .claim("role", role.name())
                .signWith(Keys.hmacShaKeyFor(secret), SignatureAlgorithm.HS256)
                .compact();
    }

    public byte[] secretKey() {
        return secret;
    }
}
