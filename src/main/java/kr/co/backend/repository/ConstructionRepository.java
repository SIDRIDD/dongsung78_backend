package kr.co.backend.repository;

import com.querydsl.core.Tuple;
import kr.co.backend.domain.Construction;
import kr.co.backend.repository.custom.CustomConstructionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ConstructionRepository extends JpaRepository<Construction, Integer>, CustomConstructionRepository {
    Page<Tuple> findListAll(Pageable pageable);
}
