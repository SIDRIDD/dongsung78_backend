package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;
import kr.co.backend.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomContactRepository {

    Page<Tuple> getAll(Pageable pageable);

    Page<Tuple> getProductContact(Pageable pageable, Integer contactType, Integer typeId);

    ResponseEntity<?> deleteByUser(User user);
}
