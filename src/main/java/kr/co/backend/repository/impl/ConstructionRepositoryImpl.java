package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.QConstruction;
import kr.co.backend.repository.custom.CustomConstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ConstructionRepositoryImpl implements CustomConstructionRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Tuple> findListAll(Pageable pageable) {
        QConstruction construction = QConstruction.construction;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        construction.constructionId,
                        construction.companyName,
                        construction.companyDetail,
                        construction.companyDescription,
                        construction.img_url,
                        construction.insertDate,
                        construction.userId.name,
                        construction.categoryId.name
                ).from(construction)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(construction.insertDate.desc());

        List<Tuple> result = query.fetch();
        long total = query.fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

}
