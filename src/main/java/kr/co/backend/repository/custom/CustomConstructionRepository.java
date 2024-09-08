package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CustomConstructionRepository {
    List<Tuple> findOne(Integer constructionId);

    Page<Tuple> findListAll(Pageable pageable);
    List<Tuple> findDetail(Integer constructionId);
}
