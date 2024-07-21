package kr.co.backend.dto.User;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class LoginResponseDto {
    private String token;
    private String email;

    private String userName;
}
