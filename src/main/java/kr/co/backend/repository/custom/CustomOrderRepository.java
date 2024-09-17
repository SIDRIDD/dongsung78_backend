package kr.co.backend.repository.custom;

import kr.co.backend.domain.User;
import org.springframework.http.ResponseEntity;

public interface CustomOrderRepository {
    ResponseEntity<?> deleteByUser(User user);
}
