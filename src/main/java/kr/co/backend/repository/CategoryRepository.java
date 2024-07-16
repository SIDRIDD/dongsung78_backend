package kr.co.backend.repository;

import com.querydsl.core.Tuple;
import kr.co.backend.repository.custom.CustomCategoryRepository;
import kr.co.backend.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long>, CustomCategoryRepository {

    List<Tuple> getAll();

}
