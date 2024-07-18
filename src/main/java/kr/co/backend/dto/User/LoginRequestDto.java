package kr.co.backend.dto.User;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequestDto {

    //이게 걍 ID임.
    private String userName;
    private String password;
}
