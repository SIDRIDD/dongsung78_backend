package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.QConstruction;
import kr.co.backend.domain.QConstructionDetail;
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
    public List<Tuple> findOne(Integer constructionId) {

        QConstruction construction = QConstruction.construction;

        List<Tuple> query = jpaQueryFactory.select(
                        construction.constructionId,
                        construction.companyName,
                        construction.insertDate,
                        construction.user.name,
                        construction.category.name
                ).from(construction)
                .where(construction.constructionId.eq(constructionId))
                .orderBy(construction.insertDate.desc())
                .fetch();

        return query;
    }

    public List<Tuple> findDetail(Integer constructionId) {
        QConstructionDetail constructionDetail = QConstructionDetail.constructionDetail;

        List<Tuple> query = jpaQueryFactory.select(
                        constructionDetail.companyDetail,
                        constructionDetail.companyDescription,
                        constructionDetail.img_url
                ).from(constructionDetail)
                .where(constructionDetail.construction.constructionId.eq(constructionId))
                .fetch();

        return query;
    }

    @Override
    public Page<Tuple> findListAll(Pageable pageable) {

        QConstruction construction = QConstruction.construction;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        construction.constructionId,
                        construction.companyCode,
                        construction.companyName,
                        construction.user.name,
                        construction.category.name,
                        construction.insertDate
                ).from(construction)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(construction.insertDate.desc());

        List<Tuple> result = query.fetch();

        long total = jpaQueryFactory
                .select(construction.count())
                .from(construction)
                .fetchOne();

        return new PageImpl<>(result, pageable, total);
    }

}
