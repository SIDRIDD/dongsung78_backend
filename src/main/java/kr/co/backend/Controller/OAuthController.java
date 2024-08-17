package kr.co.backend.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.User;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.service.OAuthService;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final RestTemplate restTemplate;

    private final UserRepository userRepository;

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expiration;
    private final OAuthService oAuthService;

    @GetMapping("/oauth/naver/redirect")
    public void NaverredirectUriProcessor(@RequestParam("code") String code,
                                          @RequestParam("state") String state,
                                          HttpServletResponse response) throws IOException {
        System.out.println("code : " + code);
        System.out.println("state : " + state);

        String grantType = "grant_type=authorization_code";
        String clientId = "client_id=FvadT3Ycf51hJ7DHEqk2";
        String clientSecret = "client_secret=jF2MwAHiTa";

        String url = "https://nid.naver.com/oauth2.0/token" +
                "?" + grantType +
                "&" + clientId +
                "&" + clientSecret +
                "&code=" + code +
                "&state=" + state;

        String body = restTemplate.getForEntity(url, String.class).getBody();
        System.out.println(body);// access, refresh 토큰 값이 들어있다.

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(body);
        String accessToken = rootNode.path("access_token").asText();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
        System.out.println("userInfoReponse 확인 : " + userInfoResponse);
        String userInfo = userInfoResponse.getBody();

        JsonNode userNode = objectMapper.readTree(userInfo);
        String email_beforePasing = userNode.path("response").path("email").asText();

        String[] parts = email_beforePasing.split("@");

        String userName = parts != null ? (parts[0]).concat("_").concat("naver") : "맞지 않는 email 형식";

        oAuthService.oauthUserSave(userName, "naver");

        String token = Jwts.builder()
                .setSubject(userName)
                .claim("name", userName.concat("_").concat("naver"))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
        tokenCookie.setMaxAge(60 * 60);
        response.addCookie(tokenCookie);

//        Cookie userNameCookie = new Cookie("userName", URLEncoder.encode(userName, "UTF-8"));
//        userNameCookie.setPath("/");
//        response.addCookie(userNameCookie);
//
//        Cookie emailCookie = new Cookie("email", URLEncoder.encode(email, "UTF-8"));
//        emailCookie.setPath("/");
//        response.addCookie(emailCookie);

        System.out.println("userName 확인 : " + userName);
        response.sendRedirect("http://localhost:3000/");

    }

    @GetMapping("/oauth/kakao/redirect")
    public void KakaoredirectUriProcessor(@RequestParam("code") String code,
                                          HttpServletResponse response) throws IOException {
        System.out.println("Kakao code: " + code);

        // 액세스 토큰 요청을 위한 파라미터 설정
        String grantType = "authorization_code";
        String clientId = "1ea96dd040d59d0fe987f530c4755afb";
        String redirectUri = "http://localhost:8080/oauth/kakao/redirect"; // 카카오 앱 설정에서 등록한 리디렉션 URI

        // 카카오 토큰 요청 URL
        String url = "https://kauth.kakao.com/oauth/token";

        // 요청 본문 설정
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", grantType);
        params.add("client_id", clientId);
        params.add("redirect_uri", redirectUri);
        params.add("code", code);

        // POST 요청 보내기
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(params, headers);
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, request, String.class);

        // 응답 본문에서 액세스 토큰 추출
        String body = responseEntity.getBody();
        System.out.println("Kakao Token Response: " + body);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode rootNode = objectMapper.readTree(body);
        String accessToken = rootNode.path("access_token").asText();

        // 사용자 정보 요청
        HttpHeaders userInfoHeaders = new HttpHeaders();
        userInfoHeaders.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> userInfoRequest = new HttpEntity<>(userInfoHeaders);

        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, userInfoRequest, String.class);
        System.out.println("Kakao User Info Response: " + userInfoResponse);

        // 사용자 정보 파싱
        JsonNode userNode = objectMapper.readTree(userInfoResponse.getBody());

        response.sendRedirect("http://localhost:3000/kakaoid");

        // 사용자 정보 처리 및 저장

    }

    @GetMapping("/oauth/kakao/token")
    public void KakaoTokenProcessor(@RequestParam("id") String userName, HttpServletResponse response)
            throws IOException {

        userName = userName.concat("_").concat("kakao");
        if (userRepository.existsByName(userName)) {
            getnerateToken(userName, response);
            getnerateRefreshToken(userName, response);
            response.sendRedirect("http://localhost:3000/");
        } else {
            oAuthService.oauthUserSave(userName, "kakao");
            getnerateToken(userName, response);
            getnerateRefreshToken(userName, response);
            response.sendRedirect("http://localhost:3000/");
        }
    }

    private void getnerateToken(String userName, HttpServletResponse response) {
        String token = Jwts.builder()
                .setSubject(userName)
                .claim("name", userName)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(60 * 60 * 15);
        response.addCookie(tokenCookie);
    }

    private void getnerateRefreshToken(String userName, HttpServletResponse response) {
        String token = Jwts.builder()
                .setSubject(userName)
                .claim("name", userName)
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        Cookie tokenCookie = new Cookie("RefreshToken", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/");
        tokenCookie.setMaxAge(60 * 60 * 15 * 10);
        response.addCookie(tokenCookie);
    }


}
