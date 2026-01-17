package com.capstoneproject.smartattendance.security;

import java.security.Key;
import java.util.*;
import java.util.function.Function;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;

import com.capstoneproject.smartattendance.dto.Role;

@Service
public class JwtService {
    @Value("${security.secret.key}")
    private String SECRET_KEY;

    private Key getSigningKey() {
            return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }
    private Claims extractAllClaims(String token) {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
    }
    private boolean isTokenExpired(String token) {
            return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
            return extractClaim(token, Claims::getExpiration);
    }




    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
            final Claims claims = extractAllClaims(token);
            return claimsResolver.apply(claims);
    }

    public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
    }

    public String extractRole(String token) {
            Claims claims = extractAllClaims(token);
            return (String)claims.get("role");
    }

    public String generateToken(String userId,Role role) {
            Map<String, Object> claims = new HashMap<>();
            claims.put("role", role);

            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(userId)
                    .setIssuedAt(new Date(System.currentTimeMillis()))
                    .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 24 * 7))// 1 week
                    .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                    .compact();
    }

    public boolean isTokenValid(String token, String username) {
            final String extractedUsername = extractUsername(token);
            return (extractedUsername.equals(username) && !isTokenExpired(token));
    }   
}



