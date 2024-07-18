package kr.co.backend.service;


import kr.co.backend.domain.*;
import kr.co.backend.dto.OrderDto;
import kr.co.backend.repository.OrderProductRepository;
import kr.co.backend.repository.OrderRepository;
import kr.co.backend.repository.ProductRepository;
import kr.co.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    private final OrderProductRepository orderProductRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    /**
     *주문
     */
    public ResponseEntity<String> order(List<OrderDto> orderDtoList) {

        for (OrderDto orderDto : orderDtoList) {
            User user = userRepository.findById(orderDto.getUserId())
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 userId 입니다."));

            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 productId 입니다."));

            Delivery delivery = new Delivery();
            delivery.setAddress(user.getAddress());

            OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), orderDto.getCount());

            Order order = Order.createOrder(user, delivery, orderProduct);

            orderRepository.save(order);
        }

        return ResponseEntity.ok().body("주문이 완료되었습니다.");
    }

    /**
     * 취소
     */

    public ResponseEntity<String> cancelOrder(Long orderId){

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 orderId 입니다."));

        order.cancel();

        return ResponseEntity.ok().body("삭제되었습니다.");
    }


    /**
     * 검색
     */





}
