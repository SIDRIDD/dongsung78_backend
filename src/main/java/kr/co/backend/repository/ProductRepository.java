package kr.co.backend.repository;

import com.querydsl.core.Tuple;
import kr.co.backend.repository.custom.CustomProductPepository;
import kr.co.backend.domain.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long>, CustomProductPepository{

    boolean existsByName(String name);
    Page<Tuple> findFromProductGetDto(Pageable pageable, Integer categoryId);

    Page<Tuple> findAllProduct(Pageable pageable);


}
