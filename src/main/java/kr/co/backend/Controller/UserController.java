package kr.co.backend.Controller;


import kr.co.backend.dto.User.UserSaveDto;
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

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody UserSaveDto userDto) {
        return userService.save(userDto);

    }


}
