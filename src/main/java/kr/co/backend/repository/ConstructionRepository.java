package kr.co.backend.repository;

import com.querydsl.core.Tuple;
import kr.co.backend.domain.Construction;
import kr.co.backend.repository.custom.CustomConstructionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface ConstructionRepository extends JpaRepository<Construction, Integer>, CustomConstructionRepository {
    List<Tuple> findOne(Integer constructionId);

    Page<Tuple> findListAll(Pageable pageable);

    List<Tuple> findDetail(Integer constructionId);
}
