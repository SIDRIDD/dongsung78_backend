package kr.co.backend.domain;

import jakarta.persistence.Embeddable;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Getter
@Builder
@Embeddable
public class Address {

    private String city;

    private String street;

    private String zipcode;

    private String detail;
}
