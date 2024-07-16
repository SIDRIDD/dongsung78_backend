package kr.co.backend.service;


import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import kr.co.backend.domain.User;
import kr.co.backend.repository.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Getter @Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
public class OAuthService {

    @Autowired
    private UserRepository userRepository;

    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private String clientId;
    private String clientSecret;
    private String clientName;
    private String authorizationGrantType;
    private String redirectUri;

    public String processOAuthLogin(String code, String provider) {
        String accessToken = getAccessTokenFromNaver(code);
        return processNaverUser(accessToken);
    }

    private String getAccessTokenFromNaver(String code) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                request,
                String.class
        );

        // JSON 파싱하여 액세스 토큰 추출 (Jackson 또는 JSON Simple 사용)
        // 예: { "access_token": "AAA...", "token_type": "bearer", "expires_in": "3600", ... }
        // 여기서는 간단하게 문자열을 추출하는 예시만 보여줌
        String accessToken = extractAccessToken(response.getBody());
        return accessToken;
    }

    private String extractAccessToken(String responseBody) {
        // JSON 파싱하여 액세스 토큰을 추출하는 로직 추가
        // 예: { "access_token": "AAA...", "token_type": "bearer", "expires_in": "3600", ... }
        return "extracted_access_token"; // 실제로는 JSON 파싱 후 반환
    }

    private String processNaverUser(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.GET,
                request,
                String.class
        );

        // JSON 파싱하여 사용자 정보 추출 (Jackson 또는 JSON Simple 사용)
        // 여기서는 간단하게 문자열을 추출하는 예시만 보여줌
        // 예: { "response": { "id": "12345", "email": "email@example.com", "name": "홍길동", ... } }
        String userInfo = response.getBody();
        String userName = extractUserName(userInfo);
        String userEmail = extractUserEmail(userInfo);

        // 사용자 정보 처리 로직 (예: DB 저장)
        User user = new User();
        user.setName(userName);
        user.setEmail(userEmail);
        user.setOauthProvider("naver");
        userRepository.save(user);

        // JWT 토큰 생성 및 반환
        String jwtToken = Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("provider", user.getOauthProvider())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 후 만료
                .signWith(key)
                .compact();

        return jwtToken;
    }

    private String extractUserName(String userInfo) {
        // JSON 파싱하여 사용자 이름을 추출하는 로직 추가
        return "extracted_user_name"; // 실제로는 JSON 파싱 후 반환
    }

    private String extractUserEmail(String userInfo) {
        // JSON 파싱하여 사용자 이메일을 추출하는 로직 추가
        return "extracted_user_email"; // 실제로는 JSON 파싱 후 반환
    }
}
