package kr.co.backend.repository.impl;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.QContactComment;
import kr.co.backend.domain.User;
import kr.co.backend.repository.custom.CustomContactRepository;
import kr.co.backend.domain.QContact;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
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

    @Override
    public ResponseEntity<?> deleteByUser(User user) {
        try {
            QContact contact = QContact.contact;
            QContactComment contactComment = QContactComment.contactComment;

            jpaQueryFactory.delete(
                            contact
                    ).where(contact.user.userId.eq(user.getUserId()))
                    .execute();

            jpaQueryFactory.delete(
                            contactComment
                    ).where(contactComment.user.userId.eq(user.getUserId()))
                    .execute();

            return ResponseEntity.ok().body("contact와 관련된 contact 댓글이 삭제되었습니다.");
        } catch (DataAccessException e){
            return ResponseEntity.internalServerError().body("데이터베이스 접근 중 오류가 발생하였습니다.");
        }
    }

}
