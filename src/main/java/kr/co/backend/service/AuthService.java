package kr.co.backend.service;

import kr.co.backend.domain.User;
import kr.co.backend.dto.User.LoginRequestDto;
import kr.co.backend.dto.User.LoginResponseDto;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        User user = userRepository.findByName(loginRequestDto.getUserName())
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 userName입니다."));

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid email or password");
        }

        // 여기서 JWT 토큰을 생성하는 로직이 들어가야 합니다.
        // 예시로 token에 "dummy-token"을 반환합니다.
        String token = jwtUtil.generateToken(user.getEmail());

        LoginResponseDto responseDto = new LoginResponseDto();
        responseDto.setToken(token);
        responseDto.setEmail(user.getEmail());
        responseDto.setName(user.getName());
        responseDto.setUserId(user.getUserId());

        return responseDto;
    }
}
