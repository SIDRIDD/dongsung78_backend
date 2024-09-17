package kr.co.backend.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.User;
import kr.co.backend.dto.User.LoginRequestDto;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public ResponseEntity<?> login(LoginRequestDto loginRequestDto, HttpServletResponse response) {
        User user = userRepository.findByName(loginRequestDto.getUserName())
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 userName입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        String token = jwtUtil.generateToken(user.getName());
        addTokenToCookie(response, token);

        String refreshToken = jwtUtil.generateToken(user.getName());
        addRefreshTokenToCookie(response, refreshToken);

        return ResponseEntity.ok().body(Map.of("message", "로그인 되었습니다.", "userName", user.getName()));
    }

    private void addRefreshTokenToCookie(HttpServletResponse response, String refreshToken) {

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setPath("/");
        cookie.setMaxAge(60 * 60 * 2); //refreshToken 2시간
        response.addCookie(cookie);
    }

    private void addTokenToCookie(HttpServletResponse response, String token) {
        Cookie cookie = new Cookie("token", token);
        cookie.setHttpOnly(true);
//        cookie.setSecure(true); -> 배포 환경에서는 https 를 사용해야 하기 때문에 이걸 추가해야함
        cookie.setPath("/");
        cookie.setMaxAge(60 * 15);
        response.addCookie(cookie);
    }
}
