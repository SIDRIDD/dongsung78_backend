package kr.co.backend.dto.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor @NoArgsConstructor
@Getter
@Builder
public class NaverDto {

    private String token;

    private String userName;

    private String email;
}
