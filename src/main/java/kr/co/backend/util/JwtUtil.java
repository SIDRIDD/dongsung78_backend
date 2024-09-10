// JwtUtil.java
package kr.co.backend.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import kr.co.backend.filter.JwtAuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;

    @Autowired
    public JwtUtil(@Value("${jwt.secret}") String secretKey){
        this.secretKey = secretKey;
    }

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public String generateToken(String name) {
        return Jwts.builder()
                .setSubject(name)
                .claim("name", name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secretKey) // 수정된 부분
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes()); // 수정된 부분
    }

    private Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(secretKey)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (SignatureException e) {
            logger.error("Invalid JWT signature", e);
            throw new IllegalArgumentException("Invalid JWT signature", e);
        }catch (Exception e) {
            logger.error("JWT token parsing failed", e);
            throw new IllegalArgumentException("JWT token parsing failed", e);
        }
    }

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    public String getUserNameFromToken(String token) {
        try {
            String username = getClaimsFromToken(token).getSubject();
            logger.info("1.Extracted username from JWT: {}", username);
            return username;
        } catch (Exception e) {
            logger.error("Failed to extract username from JWT", e);
            return null;
        }
    }

    public Integer getUserIdFromToken(String token) {
        try {
            Integer userId = Integer.parseInt(getClaimsFromToken(token).getId());
            logger.info("userId from JWT token : ", userId);
            return userId;
        } catch (Exception e) {
            logger.error("Failed to extract userId from JWT", e);
            return null;
        }
    }

    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secretKey) // 시크릿 키로 서명 확인
                    .parseClaimsJws(token) // 토큰 파싱
                    .getBody();

            // 만료 시간 확인
            if (claims.getExpiration().before(new Date())) {
                throw new IllegalArgumentException("토큰이 만료되었습니다.");
            }

            return claims.getSubject(); // 토큰이 유효하면 subject 반환
        } catch (ExpiredJwtException e) {
            throw new IllegalArgumentException("토큰이 만료되었습니다.", e);
        } catch (SignatureException e) {
            throw new IllegalArgumentException("토큰의 서명이 유효하지 않습니다.", e);
        } catch (MalformedJwtException e) {
            throw new IllegalArgumentException("잘못된 토큰 형식입니다.", e);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.", e);
        }
    }
}
