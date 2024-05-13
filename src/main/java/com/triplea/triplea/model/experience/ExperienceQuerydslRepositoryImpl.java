package com.triplea.triplea.model.experience;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triplea.triplea.dto.experience.ExperienceRequest;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Repository
public class ExperienceQuerydslRepositoryImpl implements ExperienceQuerydslRepository {
    private final JPAQueryFactory queryFactory;
    private final QExperience experience = QExperience.experience;

    public ExperienceQuerydslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }


    @Override
    public List<Experience> findExperienceList(ExperienceRequest.Search request) throws ParseException {
        BooleanBuilder builder = new BooleanBuilder();
        Date currentDate = new Date();
        if(request == null) return queryFactory.selectFrom(experience).fetch();
        if(request.getType() != null) experienceSearchBooleanBuilder(builder, request);
        if(request.getFreeTier() != null){
            if(request.getFreeTier()){
                builder.and(experience.startDate.before(currentDate)
                        .and(experience.endDate.after(currentDate)));
            }else{
                builder.and(experience.startDate.after(currentDate)
                        .or(experience.endDate.before(currentDate)));
            }
        }

        return queryFactory.selectFrom(experience)
                .where(builder)
                .fetch();
    }

    private void experienceSearchBooleanBuilder(BooleanBuilder builder, ExperienceRequest.Search request) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        if(request.getType().equals("fullName")){
            builder.and(experience.user.fullName.like("%"+request.getContent()+ "%"));
        }
        if(request.getType().equals("email")){
            builder.and(experience.user.email.like("%"+request.getContent()+ "%"));
        }
        if(request.getType().equals("freeTierStartDate")){
            builder.and(experience.startDate.eq(formatter.parse(request.getContent())));
        }
        if(request.getType().equals("freeTierEndDate")){
            builder.and(experience.endDate.eq(formatter.parse(request.getContent())));
        }
        if(request.getType().equals("memo")){
            builder.and(experience.memo.like("%"+request.getContent()+ "%"));
        }
    }
}
