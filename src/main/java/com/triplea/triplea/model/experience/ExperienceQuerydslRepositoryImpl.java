package com.triplea.triplea.model.experience;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.QUser;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.UserQuerydslRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
public class ExperienceQuerydslRepositoryImpl implements ExperienceQuerydslRepository {
    private final JPAQueryFactory queryFactory;
    private final QExperience experience = QExperience.experience;

    public ExperienceQuerydslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public Experience findExperienceByUser(User user, boolean type) {
        BooleanBuilder builder = new BooleanBuilder();

        if(type){
            builder.and(experience.useAt.eq(true));
        }

        return queryFactory.selectFrom(experience)
                .where(experience.user.eq(user))
                .where(builder)
                .fetch();
    }
}
