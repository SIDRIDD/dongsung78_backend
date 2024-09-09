package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomContactRepository {

    Page<Tuple> getAll(Pageable pageable);

    Page<Tuple> getProductContact(Pageable pageable, Integer contactType, Integer typeId);

}
