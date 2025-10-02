package com.quiverly.backend.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    private final SecretKey key;
    private final long expiration;

    public JwtUtil(@Value("${jwt.secret}")String secret,@Value("${jwt.expiration}") long expiration){
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expiration = expiration;
    }

    //make token
    public String generateToken(String username){
        return Jwts.builder().subject(username).issuedAt(new Date()).expiration(new Date(System.currentTimeMillis() + expiration)).signWith(key).compact();
    }

    //username
    public String extractUsername(String token){
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getSubject();
    }

    //validate
    public boolean validateToken(String token, String username){
        String extracted = extractUsername(token);
        return extracted.equals(username) && !isExpired(token);
    }

    public boolean isExpired(String token){
        Date exp = Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload().getExpiration();
        return exp.before(new Date());
    }

}
