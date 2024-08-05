package kr.co.backend.Controller;

import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.dto.User.NaverDto;
import kr.co.backend.service.AuthService;
import kr.co.backend.service.OAuthService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.apache.coyote.Response;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final OAuthService oAuthService;
    private final AuthService authService;

    private static final String NAVER_TOKEN_VALIDATION_URL = "https://openapi.naver.com/v1/nid/me";

    @PostMapping("/api/oauth/naver")
    public Map<String, String> naverLogin(@RequestBody Map<String, String> body, HttpServletResponse response) {
        String code = body.get("code");
        String state = body.get("state");
        String token = oAuthService.processOAuthLogin(code, "naver", state);
        return Map.of("token", token);
    }

    @PostMapping("/api/oauth/kakao")
    public Map<String, String> kakaoLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");
        System.out.println("code : " + code);
        System.out.println("state : " +  state);
        String token = oAuthService.processOAuthLogin(code, "kakao", state);
        return Map.of("token", token);
    }

    @GetMapping("/check-naver")
    public Boolean checkNaver(@RequestParam("token") String token){
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + token);

        HttpEntity<String> entity = new HttpEntity<>("parameters", headers);

        try{
            ResponseEntity<String> response = restTemplate.exchange(
                    NAVER_TOKEN_VALIDATION_URL,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            if(response.getStatusCode().is2xxSuccessful()){
                return true;
            } else {
                return false;
            }
        }catch (Exception e){
            System.out.println("naver login exception : " + e);
            return false;
        }
    }
}

@Getter @Setter
class OAuthRequest {
    private String code;
    private String provider;
    private String redirectUri;

}

class OAuthResponse {
    private String token;

    public OAuthResponse(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
