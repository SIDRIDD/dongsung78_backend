package kr.co.backend.dto.Order;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {

    private Long productId;

    private Integer count;

    private String userName;

    private String phoneNumber;

    private String roadAddress;

    private String detailAddress;

    private String zipCode;

    private String request;


}
