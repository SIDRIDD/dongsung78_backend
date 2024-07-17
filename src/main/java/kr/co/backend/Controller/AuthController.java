package kr.co.backend.Controller;

import kr.co.backend.service.OAuthService;
import lombok.Getter;
import lombok.Setter;
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
        String token = oAuthService.processOAuthLogin(request.getCode(), request.getProvider(), request.getRedirectUri());
        return ResponseEntity.ok(new OAuthResponse(token));
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
