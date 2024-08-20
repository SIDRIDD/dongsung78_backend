package kr.co.backend.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter
@Builder
@Embeddable
public class Address {

    private String roadAddress;

    private String detailAddress;

    private String zipcode;

}
