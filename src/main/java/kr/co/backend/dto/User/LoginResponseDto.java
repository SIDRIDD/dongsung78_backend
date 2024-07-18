package kr.co.backend.dto.User;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String email;
    private String name;

    private Integer userId;
}
