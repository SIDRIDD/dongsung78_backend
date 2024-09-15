package kr.co.backend.repository.impl;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.QContactComment;
import kr.co.backend.repository.custom.CustomContactCommentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;


@Repository
@RequiredArgsConstructor
public class ContactCommentRepositoryImpl implements CustomContactCommentRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public Integer countComment(Integer integer) {
        QContactComment contactComment = QContactComment.contactComment;



        return null;
    }
}
