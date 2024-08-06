package kr.co.backend.Controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.User;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import org.springframework.http.HttpHeaders;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@RestController
@RequiredArgsConstructor
public class OAuthController {

    private final RestTemplate restTemplate;

    @Value("${jwt.secret}")
    private String secretKey;

    @GetMapping("/oauth/redirect")
    public void redirectUriProcessor(@RequestParam("code") String code,
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

        System.out.println("테스트1");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String userInfoUrl = "https://openapi.naver.com/v1/nid/me";
        System.out.println("테스트3");
        ResponseEntity<String> userInfoResponse = restTemplate.exchange(userInfoUrl, HttpMethod.GET, entity, String.class);
        System.out.println("테스트4");
        System.out.println("userInfoReponse 확인 : " + userInfoResponse);
        String userInfo = userInfoResponse.getBody();

        System.out.println("테스트2");

        JsonNode userNode = objectMapper.readTree(userInfo);
        String email = userNode.path("response").path("email").asText();
        String userName = userNode.path("response").path("nickname").asText();

        String token = Jwts.builder()
                .setSubject("userDetails")
                        .claim("email", email)
                                .claim("userName", userName)
                                        .signWith(SignatureAlgorithm.HS256, secretKey)
                                                .compact();


        Cookie tokenCookie = new Cookie("accessToken", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setPath("/"); // 모든 경로에서 쿠키 접근 가능
        tokenCookie.setMaxAge(60*60);
        response.addCookie(tokenCookie);

//        Cookie userNameCookie = new Cookie("userName", URLEncoder.encode(userName, "UTF-8"));
//        userNameCookie.setPath("/");
//        response.addCookie(userNameCookie);
//
//        Cookie emailCookie = new Cookie("email", URLEncoder.encode(email, "UTF-8"));
//        emailCookie.setPath("/");
//        response.addCookie(emailCookie);

        System.out.println("email 확인 : " + email + "userName 확인 : " + userName);
        response.sendRedirect("http://localhost:3000?email="+URLEncoder.encode(email, "UTF-8")+"&userName="+URLEncoder.encode(userName, "UTF-8"));

    }

}
