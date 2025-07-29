package com.example.blood_donation.service;

import com.example.blood_donation.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Service
public class TokenService {

    private final String SECRET_KEY = "4bb6d1dfbafb64a681139d1586b6f1160d18159afd57c8c79136d7490630407c";

    private SecretKey getSigninKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Sinh JWT Token cho user
     */
    public String generateToken(User user) {
        return generateToken(user, false); // mặc định không có remember me
    }

    /**
     * Sinh JWT Token cho user với tùy chọn remember me
     */
    public String generateToken(User user, boolean rememberMe) {
        long expirationTime;
        if (rememberMe) {
            expirationTime = 30L * 24 * 60 * 60 * 1000; // 30 ngày cho login có remember me
        } else {
            expirationTime = 24 * 60 * 60 * 1000; // 24 giờ cho login thông thường
        }

        return Jwts.builder()
                .subject(user.getUsername())
                .claim("authorities", user.getRole().name())
                .claim("rememberMe", rememberMe) // Add remember me flag to token
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigninKey())
                .compact();
    }

    /**
     * Trích xuất tất cả claims từ JWT token
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigninKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Trích xuất một claim bất kỳ
     */
    public <T> T extractClaim(String token, Function<Claims, T> resolver) {
        Claims claims = extractAllClaims(token);
        return resolver.apply(claims);
    }

    /**
     * Trích xuất username (subject) từ token
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Trích xuất thời điểm hết hạn
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Kiểm tra token hết hạn hay chưa
     */
    public boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Trích xuất remember me flag từ token
     */
    public boolean extractRememberMe(String token) {
        return extractClaim(token, claims -> claims.get("rememberMe", Boolean.class));
    }
}
