package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.repository.custom.CustomContactRepository;
import kr.co.backend.domain.QContact;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContactRepositoryImpl implements CustomContactRepository {

    private final JPAQueryFactory jpaQueryFactory;


    @Override
    public Page<Tuple> getAll(Pageable pageable) {

        QContact contact = QContact.contact;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        contact.id,
                        contact.title,
                        contact.description,
                        contact.user.userId,
                        contact.user.name,
                        contact.createdAt
                ).from(contact)
                .where(contact.contactType.eq(0))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(contact.createdAt.desc());

        List<Tuple> result = query.fetch();

        long total = jpaQueryFactory.select(
                        contact.id
                ).from(contact)
                .where(contact.contactType.eq(0))
                .fetchCount();


        return new PageImpl<>(result, pageable, total);
    }

    @Override
    public Page<Tuple> getProductContact(Pageable pageable, Integer contactType, Integer typeId) {
        QContact contact = QContact.contact;

        JPAQuery<Tuple> query = jpaQueryFactory.select(
                        contact.id,
                        contact.title,
                        contact.description,
                        contact.user.userId,
                        contact.user.name,
                        contact.createdAt
                ).from(contact)
                .where(contact.contactType.eq(contactType).and(contact.typeId.eq(typeId)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(contact.createdAt.desc());

        List<Tuple> result = query.fetch();

        long total = jpaQueryFactory.select(
                        contact.id
                ).from(contact)
                .where(contact.contactType.eq(1))
                .fetchCount();

        return new PageImpl<>(result, pageable, total);
    }

}
