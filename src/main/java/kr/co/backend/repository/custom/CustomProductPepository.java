package kr.co.backend.repository.custom;

import com.querydsl.core.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomProductPepository {

    Page<Tuple> findFromProductGetDto(Pageable pageable, Integer categoryId);

    Page<Tuple> findAllProduct(Pageable pageable);

}
