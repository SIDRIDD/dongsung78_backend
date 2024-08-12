package kr.co.backend.repository.impl;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.backend.domain.QUser;
import kr.co.backend.repository.custom.CustomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements CustomUserRepository {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public boolean existsByNameAndOauthProvider(String userName, String provider) {
        QUser user = QUser.user;

        JPAQuery query = jpaQueryFactory.select(
                        user.userId
                ).from(user)
                .where(user.oauthProvider.eq(provider).and(user.name.eq(userName)));

        Integer userId = (Integer) query.fetchFirst();

        return userId != null;
    }
}
