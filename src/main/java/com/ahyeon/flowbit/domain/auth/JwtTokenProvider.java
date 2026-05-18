package com.ahyeon.flowbit.domain.auth;

import com.ahyeon.flowbit.domain.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Date;

@Component
public class JwtTokenProvider {

    private static final String SECRET_KEY =
            "flowbit-jwt-secret-key-for-local-development-must-be-at-least-32-bytes";

    private static final long ACCESS_TOKEN_EXPIRATION_MILLISECONDS =
            Duration.ofHours(1).toMillis();

    private final SecretKey key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));

    public String createAccessToken(User user) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + ACCESS_TOKEN_EXPIRATION_MILLISECONDS);

        return Jwts.builder()
                .subject(String.valueOf(user.getId()))
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(expiration)
                .signWith(key)
                .compact();
    }
}