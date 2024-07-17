package kr.co.backend.dto;

import kr.co.backend.domain.Order;
import kr.co.backend.domain.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OrderDto {

    private Long productId;

    private Integer userId;

    private Integer count;

}
