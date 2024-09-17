package kr.co.backend.repository;

import kr.co.backend.domain.Order;
import kr.co.backend.domain.User;
import kr.co.backend.repository.custom.CustomOrderRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;

public interface OrderRepository extends JpaRepository<Order, Long>, CustomOrderRepository {
    ResponseEntity<?> deleteByUser(User user);
}
