package kr.co.backend.Controller;


import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.Delivery;
import kr.co.backend.domain.Role;
import kr.co.backend.domain.User;
import kr.co.backend.dto.User.*;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.service.AuthService;
import kr.co.backend.service.UserService;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final AuthService authService;
    private final JwtUtil jwtUtil;

    private final PasswordEncoder passwordEncoder;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody UserSaveDto userDto) {
        userDto.setRole(Role.CUSTOMER);
        return userService.save(userDto);

    }

    @PutMapping("/update-user")
    public ResponseEntity<?> updateAdmin(@RequestBody UserUpdateDto userUpdateDto) {
        return userService.update(userUpdateDto);

    }

    @GetMapping("/get-user")
    public UserUpdateDto getUser(@CookieValue("token") String token){
        String userName = jwtUtil.getUserNameFromToken(token);

        return userService.getUser(userName);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        if (userRepository.existsByName(loginRequestDto.getUserName())) {
            return authService.login(loginRequestDto, response);

        } else {
            return ResponseEntity.badRequest().body("존재 하지 않는 ID입니다.");
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> handleLogout(HttpServletResponse response) {
        System.out.println("logout");
        // accessToken 쿠키 삭제
        Cookie accessTokenCookie = new Cookie("token", null);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setMaxAge(0); // 쿠키를 즉시 만료시킴
//        accessTokenCookie.setSecure(true); // HTTPS에서만 전송

        // refreshToken 쿠키 삭제
        Cookie refreshTokenCookie = new Cookie("refreshToken", null);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setMaxAge(0); // 쿠키를 즉시 만료시킴
//        refreshTokenCookie.setSecure(true);


        // 쿠키를 응답에 추가해 브라우저에서 삭제되도록 함
        response.addCookie(accessTokenCookie);
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/check-signup")
    public Boolean checkSignUp(@RequestParam("username") String userName) {
        if (!userRepository.existsByName(userName)) return Boolean.TRUE;
        else return Boolean.FALSE;
    }

    //    @GetMapping("/check")
    @GetMapping("/refresh-check")
    public ResponseEntity<?> checkLoginStatus(@CookieValue(value = "refreshToken", required = false) @Nullable String token, HttpServletResponse response) {
        if(token == null){
            return ResponseEntity.badRequest().body("로그인 안된 상태");
        }
        boolean refreshTokenIsValid = jwtUtil.isTokenExpired(token);

        if (token != null && !token.isEmpty() && !refreshTokenIsValid) {
            try {
                String username = jwtUtil.validateToken(token);
                if (username != null) {
                    String accessToken = jwtUtil.generateToken(username);
                    setAccessToken(accessToken, response);
                    return ResponseEntity.status(HttpStatus.OK).body("Token Generated.");
                } else {
                    return ResponseEntity.status(401).body("Invalid user");
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } else {
            return ResponseEntity.status(403).body("Expired refreshToken");
        }
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> checkRefreshTokenStatus(@CookieValue(value = "refreshToken", required = false) @Nullable String refreshToken, HttpServletResponse response) {
        if (refreshToken != null && !refreshToken.isEmpty() && jwtUtil.isTokenExpired(refreshToken)) {
            try {
                String username = jwtUtil.validateToken(refreshToken);
                if (username != null) {
                    String accessToken = jwtUtil.generateToken(username);
                    setAccessToken(accessToken, response);
                    return ResponseEntity.ok().body("Invalid Token");
                } else {
                    return ResponseEntity.status(401).body("Invalid user");
                }
            } catch (Exception e) {
                return ResponseEntity.status(401).body("Invalid token");
            }
        } else {
            return ResponseEntity.status(401).body("Not authenticated");
        }
    }

    private void setAccessToken(String accessToken, HttpServletResponse response) {
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15); // token 15분
        response.addCookie(cookie);

    }

    @GetMapping("/delivery-info")
    public OldDeliveryDto getOldDelivery(HttpServletRequest request) {
        String jwt = "";
        String userName = "";
        OldDeliveryDto oldDeliveryDto = null;
        Cookie[] cookies = request.getCookies();

        for (Cookie cookie : cookies) {
            if ("token".equals(cookie.getName())) {
                jwt = cookie.getValue();

                userName = jwtUtil.getUserNameFromToken(jwt);

                User user = userRepository.findByName(userName).orElseThrow(() -> new RuntimeException("토큰이 유효하지 않습니다.")); //유저가 없다고 해야되는건지 토큰이 유효하지 않다고 해야 하는건지 헷갈림
                oldDeliveryDto = OldDeliveryDto.builder()
                        .roadAddress(user.getAddress().getRoadAddress())
                        .detailAddress(user.getAddress().getDetailAddress())
                        .zipCode(user.getAddress().getZipcode())
                        .build();

            }
        }

        return oldDeliveryDto;
    }

}
