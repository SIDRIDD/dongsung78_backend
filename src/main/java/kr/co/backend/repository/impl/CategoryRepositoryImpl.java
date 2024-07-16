package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.repository.custom.CustomCategoryRepository;
import kr.co.backend.domain.QCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;


@RequiredArgsConstructor
@Repository
public class CategoryRepositoryImpl implements CustomCategoryRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<Tuple> getAll() {
        QCategory category = QCategory.category;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                category.categoryId,
                category.name,
                category.description,
                category.imgUrl
        ).from(category);

        List<Tuple> results = query.fetch();

        return results;
    }
}
