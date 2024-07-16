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
                        contact.user.userId
                ).from(contact)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize());

        List<Tuple> result = query.fetch();

        long total = result.size();


        return new PageImpl<>(result, pageable, total);
    }

}
