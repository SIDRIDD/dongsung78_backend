package kr.co.backend.dto.User;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OldDeliveryDto {

    private String roadAddress;

    private String detailAddress;

    private String zipCode;

}
