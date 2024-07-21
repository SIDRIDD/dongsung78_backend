package kr.co.backend.Controller;


import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.Role;
import kr.co.backend.domain.User;
import kr.co.backend.dto.User.LoginRequestDto;
import kr.co.backend.dto.User.LoginResponseDto;
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
            LoginResponseDto responseDto = authService.login(loginRequestDto, response);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().body("존재 하지 않는 ID입니다.");
        }
    }

    @GetMapping("/check")
    public ResponseEntity<?> checkLoginStatus(@CookieValue(value = "token", required = false) String token) {
        if (token != null && !token.isEmpty()) {
            try {
                String username = jwtUtil.validateToken(token);
                User user = userRepository.findByName(username).orElseThrow(() -> new RuntimeException("존재하지 않는 ID입니다."));
                if (user != null) {
                    LoginResponseDto responseDto = new LoginResponseDto(token, user.getEmail(), user.getName());
                    return ResponseEntity.ok(responseDto);
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

}
