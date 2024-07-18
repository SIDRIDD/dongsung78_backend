package kr.co.backend.Controller;


import kr.co.backend.domain.Role;
import kr.co.backend.dto.User.LoginRequestDto;
import kr.co.backend.dto.User.LoginResponseDto;
import kr.co.backend.dto.User.UserSaveDto;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.service.AuthService;
import kr.co.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    private final AuthService authService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody UserSaveDto userDto) {
        userDto.setRole(Role.CUSTOMER);
        return userService.save(userDto);

    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDto loginRequestDto) {
        if (userRepository.existsByName(loginRequestDto.getUserName())) {
            LoginResponseDto responseDto = authService.login(loginRequestDto);
            return ResponseEntity.ok(responseDto);
        } else {
            return ResponseEntity.badRequest().body("존재 하지 않는 ID입니다."); //ID가 userName을 뜻함
        }
    }

}
