package kr.co.backend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Service
@Getter @Setter
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.naver")
@Transactional
public class OAuthService {

    @Autowired
    private UserRepository userRepository;

    private static final SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    private String clientId;
    private String clientSecret;
    private String clientName;
    private String authorizationGrantType;
    private String redirectUri;

    public String processOAuthLogin(String code, String provider, String redirectUri) {
        String accessToken;
        switch (provider) {
            case "naver":
                accessToken = getAccessTokenFromNaver(code, redirectUri);
                return processNaverUser(accessToken);
            case "kakao":
                accessToken = getAccessTokenFromKakao(code, redirectUri);
                return processKakaoUser(accessToken);
            case "google":
                accessToken = getAccessTokenFromGoogle(code, redirectUri);
                return processGoogleUser(accessToken);
            default:
                throw new IllegalArgumentException("Unsupported provider: " + provider);
        }
    }

    private String processGoogleUser(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://www.googleapis.com/oauth2/v1/userinfo",
                HttpMethod.GET,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get user info from Google: " + response.getBody());
        }

        return parseUser(response.getBody());
    }


    private String getAccessTokenFromGoogle(String code, String redirectUri) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("code", code);
        params.add("redirect_uri", redirectUri);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);

        ResponseEntity<String> response = rt.exchange(
                "https://oauth2.googleapis.com/token",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token from Google: " + response.getBody());
        }

        return extractAccessToken(response.getBody());
    }


    private String processKakaoUser(String accessToken) {
        RestTemplate rt = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);

        ResponseEntity<String> response = rt.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.GET,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get user info from Kakao: " + response.getBody());
        }

        return parseUser(response.getBody());
    }


    private String getAccessTokenFromKakao(String code, String redirectUri) {
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
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                request,
                String.class
        );

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token from Kakao: " + response.getBody());
        }

        return extractAccessToken(response.getBody());
    }


    private String getAccessTokenFromNaver(String code, String redirectUri) {
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

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get access token from Naver: " + response.getBody());
        }

        return extractAccessToken(response.getBody());
    }

    private String extractAccessToken(String responseBody) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(responseBody);
            return rootNode.path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to extract access token", e);
        }
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

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to get user info from Naver: " + response.getBody());
        }

        return parseUser(response.getBody());
    }

    private String parseUser(String userInfo) {
        try {
            System.out.println("아니 여기를 한번만 타야지 왤케 문제가 생기는거야 ㅁㅊ");
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(userInfo);
            JsonNode responseNode;

            if (userInfo.contains("kakao_account")) {
                // Kakao
                responseNode = rootNode.path("kakao_account");
            } else if (userInfo.contains("email")) {
                // Google
                responseNode = rootNode;
            } else {
                // Naver
                responseNode = rootNode.path("response");
            }

            String userName = responseNode.path("name").asText();
            String userEmail = responseNode.path("email").asText();

            Optional<User> existingUser = userRepository.findByEmail(userEmail);
            User user;
            if (existingUser.isPresent()) {
                System.out.println("if 문");
                user = existingUser.get();
                user.setName(userName); // 필요에 따라 사용자 정보를 업데이트합니다.
                user.setOauthProvider("naver");
                userRepository.save(user); // 업데이트된 정보를 저장합니다.
            } else {
                System.out.println("else 문");
                user = new User();
                user.setName(userName);
                user.setEmail(userEmail);
                user.setOauthProvider("naver");
                userRepository.save(user); // 사용자 정보를 저장합니다.
            }

            return generateJwtToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse user info", e);
        }
    }


    private String generateJwtToken(User user) {
        // 디버깅 로그 추가
        System.out.println("generateJwtToken 메서드 호출됨");

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("name", user.getName())
                .claim("provider", user.getOauthProvider())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1시간 후 만료
                .signWith(key)
                .compact();
    }
}
