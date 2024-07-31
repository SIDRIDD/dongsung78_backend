package kr.co.backend.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import kr.co.backend.repository.OrderProductRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@Table(name = "order_tb")
@Entity
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderProduct> orderProducts = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    private LocalDateTime orderDate;

    @Enumerated(EnumType.STRING)
    private Status status;

    @PrePersist
    protected void onCreate() {
        if (this.orderDate == null) {
            this.orderDate = LocalDateTime.now();
        }
    }

    public void setUser(User user) {
        this.user = user;
        user.getOrders().add(this);
    }

    public void addOrderProduct(OrderProduct orderProduct){
        orderProducts.add(orderProduct);
        orderProduct.setOrder(this);
    }

    public void setDelivery(Delivery delivery){
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    protected Order(){
    }

    public static Order createOrder(User user, Delivery delivery, OrderProduct... orderProducts){
        Order order = new Order();

        order.setUser(user);
        order.setDelivery(delivery);
        for(OrderProduct orderProduct : orderProducts){
            order.addOrderProduct(orderProduct);
        }

        order.setStatus(Status.ORDER);
        order.setOrderDate(LocalDateTime.now());
        return order;
    }

    public void cancel(){
        if(delivery.getStatus() == DeliveryStatus.COMP){
            throw new IllegalStateException("이미 배송된 상품은 취소가 불가능합니다.");
        }
        this.setStatus(Status.CANCEL);
        for(OrderProduct orderProduct : orderProducts){
            orderProduct.cancel();
        }
    }

    public int getTotalPrice(){
        int totalPrice = 0;
        for(OrderProduct orderProduct : orderProducts){
            totalPrice += orderProduct.getTotalPrice();
        }

        return totalPrice;
    }
}
