package com.triplea.triplea.model.user;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triplea.triplea.dto.user.UserRequest;
import org.springframework.stereotype.Repository;
import com.triplea.triplea.model.user.QUser;
import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class UserQuerydslRepositoryImpl implements UserQuerydslRepository {
    private final JPAQueryFactory queryFactory;
    private final QUser user = QUser.user;

    public UserQuerydslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<User> findUserByType(UserRequest.UserSearch request) {
        BooleanBuilder builder = new BooleanBuilder();

        if(request.getType().equals("fullName")){
            builder.and(user.fullName.like(request.getContent()));
        }
        if(request.getType().equals("email")){
            builder.and(user.email.like(request.getContent()));
        }
        if(request.getType().equals("membership")){
            builder.and(user.membership.eq(User.Membership.valueOf(request.getContent())));
        }
        if(request.getType().equals("memberRole")){
            builder.and(user.memberRole.eq(User.MemberRole.valueOf(request.getContent())));
        }

        return queryFactory.selectFrom(user)
                .where(builder)
                .fetch();
    }
}
