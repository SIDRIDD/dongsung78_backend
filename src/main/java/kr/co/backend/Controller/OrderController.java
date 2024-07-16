package kr.co.backend.Controller;


import kr.co.backend.dto.OrderDto;
import kr.co.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/save")
    public ResponseEntity<String> order(@RequestBody List<OrderDto> orderDto){
        return orderService.order(orderDto);
    }

    @DeleteMapping("/delete/{productId}")
    public ResponseEntity<String> delete(@PathVariable("productId") Long orderId){
        return orderService.cancelOrder(orderId);
    }
}
