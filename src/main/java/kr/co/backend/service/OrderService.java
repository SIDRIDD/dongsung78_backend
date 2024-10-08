package kr.co.backend.service;


import jakarta.persistence.EntityManager;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import kr.co.backend.domain.*;
import kr.co.backend.dto.Order.OrderDto;
import kr.co.backend.repository.OrderProductRepository;
import kr.co.backend.repository.OrderRepository;
import kr.co.backend.repository.ProductRepository;
import kr.co.backend.repository.UserRepository;
import kr.co.backend.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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

    public ResponseEntity<String> order(List<OrderDto> orderDtoList, HttpServletRequest request) {
        String userName = getUserName(request);

        User user = userRepository.findByName(userName)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 userId 입니다."));

        OrderDto firstOrderDto = orderDtoList.get(0);

        Address address = Address.builder()
                .roadAddress(firstOrderDto.getRoadAddress())
                .detailAddress(firstOrderDto.getDetailAddress())
                .zipcode(firstOrderDto.getZipCode())
                .build();

        if (user.getOauthProvider() != null) {

            user.setAddress(address);
            user.setPhoneNumber(firstOrderDto.getPhoneNumber());
            userRepository.save(user);
            entityManager.flush();

        } else {

            if (user.getAddress().getRoadAddress() == null) {
                user.setAddress(address);
                userRepository.save(user);
            }
        }

        Delivery delivery = Delivery.builder()
                .address(address)
                .detail(firstOrderDto.getRequest())
                .status(DeliveryStatus.READY)
                .build();

        Order order = Order.createOrder(user, delivery);

        for (OrderDto orderDto : orderDtoList) {
            Product product = productRepository.findById(orderDto.getProductId())
                    .orElseThrow(() -> new RuntimeException("존재 하지 않는 productId 입니다."));

            OrderProduct orderProduct = OrderProduct.createOrderProduct(product, product.getPrice(), orderDto.getCount(), order);

            orderProductRepository.save(orderProduct);

        }
        orderRepository.save(order);


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

    public ResponseEntity<?> deleteOrder(User user){
        if(orderRepository.deleteByUser(user).getStatusCode() == HttpStatus.OK){
            return ResponseEntity.ok().body("주문이 삭제되었습니다.");
        } else {
            return orderRepository.deleteByUser(user);
        }
    }


    public ResponseEntity<String> cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("존재 하지 않는 orderId 입니다."));

        order.cancel();

        return ResponseEntity.ok().body("삭제되었습니다.");
    }


}
