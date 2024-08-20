package kr.co.backend.Controller;


import jakarta.annotation.Nullable;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.Delivery;
import kr.co.backend.domain.Role;
import kr.co.backend.domain.User;
import kr.co.backend.dto.User.LoginRequestDto;
import kr.co.backend.dto.User.LoginResponseDto;
import kr.co.backend.dto.User.OldDeliveryDto;
import kr.co.backend.dto.User.UserSaveDto;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.service.AuthService;
import kr.co.backend.service.UserService;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody UserSaveDto userDto) {
        userDto.setRole(Role.CUSTOMER);
        return userService.save(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse response) {
        if (userRepository.existsByName(loginRequestDto.getUserName())) {
            return authService.login(loginRequestDto, response);

        } else {
            return ResponseEntity.badRequest().body("존재 하지 않는 ID입니다.");
        }
    }

    private void setCookie(String refreshToken, String token, HttpServletResponse response) {

        Cookie cookie_refresh = new Cookie("refreshToken", refreshToken);
        cookie_refresh.setHttpOnly(true);
        cookie_refresh.setPath("/");
        cookie_refresh.setMaxAge(69 * 60 * 15 * 10);
        response.addCookie(cookie_refresh);

        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 15);
        response.addCookie(cookie);
    }


    @GetMapping("/check-signup")
    public Boolean checkSignUp(@RequestParam("username") String userName) {
        if (!userRepository.existsByName(userName)) return Boolean.TRUE;
        else return Boolean.FALSE;
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(@CookieValue(value = "token", required = false) @Nullable String token, HttpServletResponse response) {
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.validateToken(token);
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
        System.out.println("제너레이트토큰!!!!!!!!!!!!!");
        Cookie cookie = new Cookie("token", accessToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 15);
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
