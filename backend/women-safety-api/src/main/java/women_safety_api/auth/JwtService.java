package women_safety_api.auth;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.Claims;

@Service
public class JwtService {
	
	

    private final SecretKey secretKey =
            Keys.hmacShaKeyFor(
                    "women-safety-band-secret-key-2026-secure".getBytes()
            );

    private final long expirationTime = 1000 * 60 * 60; // 1 hour
    

    public String generateToken(User user) {

        Date now = new Date();

        Date expirationDate =
                new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .subject(user.getId())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .issuedAt(now)
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }
    
    public String extractUserId(String token) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        return claims.getSubject();
    }
    
    public boolean isTokenValid(String token, User user) {

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String userId = claims.getSubject();

        Date expirationDate = claims.getExpiration();

        boolean tokenBelongsToUser =
                userId.equals(user.getId());

        boolean tokenIsNotExpired =
                expirationDate.after(new Date());

        return tokenBelongsToUser && tokenIsNotExpired;
    }
}