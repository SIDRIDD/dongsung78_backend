package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.repository.custom.CustomProductPepository;
import kr.co.backend.domain.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements CustomProductPepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Page<Tuple> findFromProductGetDto(Pageable pageable, Integer categoryId) {
        QProduct product = QProduct.product;

        BooleanExpression condition = null;

        if (categoryId != null) {
            condition = product.category.categoryId.eq(categoryId);
        }

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        product.productId,
                        product.name,
                        product.description,
                        product.price,
                        product.stock,
                        product.imageUrl,
                        product.category.name,
                        product.priority
                ).from(product)
                .where(condition)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(product.getType(), product.getMetadata());
            query.orderBy(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty())
            ));
        }

        List<Tuple> queryResults = query.fetch();

        long total = jpaQueryFactory.select(product.count())
                .from(product)
                .where(condition)
                .fetchOne();

        return new PageImpl<>(queryResults, pageable, total);
    }

    @Override
    public Page<Tuple> findAllProduct(Pageable pageable) {
        QProduct product = QProduct.product;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        product.productId,
                        product.name,
                        product.description,
                        product.price,
                        product.stock,
                        product.imageUrl,
                        product.category.name,
                        product.priority
                ).from(product)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        for (Sort.Order order : pageable.getSort()) {
            PathBuilder pathBuilder = new PathBuilder(product.getType(), product.getMetadata());
            query.orderBy(new OrderSpecifier(
                    order.isAscending() ? Order.ASC : Order.DESC,
                    pathBuilder.get(order.getProperty())
            ));
        }

        List<Tuple> queryResults = query.fetch();

        long total = jpaQueryFactory.select(product.count())
                .from(product)
                .fetchOne();

        return new PageImpl<>(queryResults, pageable, total);
    }

}
