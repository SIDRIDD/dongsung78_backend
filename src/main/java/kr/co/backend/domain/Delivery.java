package kr.co.backend.domain;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor @NoArgsConstructor
@Entity @Builder
@Getter @Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    @Column(insertable = false, updatable = false)
    private String detail;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status; //READY, COMP
}
