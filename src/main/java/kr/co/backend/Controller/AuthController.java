package kr.co.backend.Controller;

import kr.co.backend.service.OAuthService;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/api/oauth/naver")
    public Map<String, String> naverLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");
        String token = oAuthService.processOAuthLogin(code, "naver", state);
        return Map.of("token", token);
    }

    @PostMapping("/api/oauth/kakao")
    public Map<String, String> kakaoLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");
        String token = oAuthService.processOAuthLogin(code, "kakao", state);
        return Map.of("token", token);
    }

    @PostMapping("/api/oauth/google")
    public Map<String, String> googleLogin(@RequestBody Map<String, String> body) {
        String code = body.get("code");
        String state = body.get("state");
        String token = oAuthService.processOAuthLogin(code, "google", state);
        return Map.of("token", token);
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
