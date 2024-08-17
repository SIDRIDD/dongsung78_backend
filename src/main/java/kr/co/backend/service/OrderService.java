package kr.co.backend.service;


import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.co.backend.domain.*;
import kr.co.backend.dto.OrderDto;
import kr.co.backend.repository.OrderProductRepository;
import kr.co.backend.repository.OrderRepository;
import kr.co.backend.repository.ProductRepository;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.util.JwtUtil;
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

    private final JwtUtil jwtUtil;

    private final EntityManager entityManager;

    /**
     * 주문
     */
    public ResponseEntity<String> order(List<OrderDto> orderDtoList, HttpServletRequest request) {
        String userName = getUserName(request);

        for (OrderDto orderDto : orderDtoList) {
            User user = userRepository.findByName(userName)
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 userId 입니다."));

            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 productId 입니다."));


            if (user.getOauthProvider() != null) {
                Address address = Address.builder()
                        .city(orderDto.getCity())
                        .street(orderDto.getStreet())
                        .zipcode(orderDto.getZipCode())
                        .build();
                user.setAddress(address);
                userRepository.save(user);
                entityManager.flush();

                Delivery delivery = Delivery.builder()
                        .address(address)
                        .detail(orderDto.getRequest())
                        .status(DeliveryStatus.READY)
                        .build();

                OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), orderDto.getCount());

                Order order = Order.createOrder(user, delivery, orderProduct);

                orderRepository.save(order);
            } else {
                Delivery delivery = Delivery.builder()
                        .address(user.getAddress())
                        .detail(orderDto.getRequest())
                        .status(DeliveryStatus.READY)
                        .build();

                OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), orderDto.getCount());

                Order order = Order.createOrder(user, delivery, orderProduct);

                orderRepository.save(order);
            }
        }

        return ResponseEntity.ok().body("주문이 완료되었습니다.");
    }

    private String getJwtFromCookies(Cookie[] cookies) {
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private String getUserName(HttpServletRequest request) {
        String jwtToken = getJwtFromCookies(request.getCookies());
        if (jwtToken == null) {
            return null;
        }

        String userName = jwtUtil.getUserNameFromToken(jwtToken);
        if (userName == null) {
            return null;
        }

        return userName;
    }

    /**
     * 취소
     */

    public ResponseEntity<String> cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 orderId 입니다."));

        order.cancel();

        return ResponseEntity.ok().body("삭제되었습니다.");
    }


    /**
     * 검색
     */


}
