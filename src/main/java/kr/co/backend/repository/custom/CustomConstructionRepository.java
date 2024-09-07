package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomConstructionRepository {
    Page<Tuple> findListAll(Pageable pageable);
}
