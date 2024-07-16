package kr.co.backend.Controller;

import kr.co.backend.service.OAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private OAuthService oAuthService;

    @PostMapping("/oauth")
    public ResponseEntity<?> handleOAuthLogin(@RequestBody OAuthRequest request) {
        String token = oAuthService.processOAuthLogin(request.getAccessToken(), request.getProvider());
        return ResponseEntity.ok(new OAuthResponse(token));
    }
}

class OAuthRequest {
    private String accessToken;
    private String provider;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
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
