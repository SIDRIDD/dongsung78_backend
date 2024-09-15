package kr.co.backend.dto.User;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Builder
public class UserUpdateDto {

    private Integer userId;

    private String name;

    private AddressDto address;

    private String email;


    private String phoneNumber;


}
