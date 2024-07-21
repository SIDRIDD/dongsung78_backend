// JwtUtil.java
package kr.co.backend.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
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

    public String generateToken(String name) {
        return Jwts.builder()
                .setSubject(name)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS512, secretKey) // 수정된 부분
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secretKey.getBytes()); // 수정된 부분
    }

    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(secretKey) // 수정된 부분
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isTokenExpired(String token) {
        return getClaimsFromToken(token).getExpiration().before(new Date());
    }

    public String getUserNameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }

    public String validateToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(secretKey) // 수정된 부분
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }
}
