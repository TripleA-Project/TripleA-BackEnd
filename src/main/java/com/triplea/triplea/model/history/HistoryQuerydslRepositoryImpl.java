package com.triplea.triplea.model.history;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triplea.triplea.core.util.timestamp.Timestamped;
import com.triplea.triplea.model.user.User;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class HistoryQuerydslRepositoryImpl implements HistoryQuerydslRepository {
    private final JPAQueryFactory queryFactory;
    private final QHistory history = QHistory.history;

    public HistoryQuerydslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }

    @Override
    public List<LocalDate> findDateByCreatedAtAndUser(int year, int month, User user) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate minDate = yearMonth.atDay(1);
        LocalDate maxDate = yearMonth.atEndOfMonth();
        ZonedDateTime startDateTime = ZonedDateTime.of(minDate, LocalTime.MIN, Timestamped.SEOUL_ZONE_ID);
        ZonedDateTime endDateTime = ZonedDateTime.of(maxDate, LocalTime.MAX, Timestamped.SEOUL_ZONE_ID);

        return queryFactory.select(history.createdAt)
                .from(history)
                .where(history.createdAt.between(startDateTime, endDateTime)
                        .and(history.user.eq(user)))
                .orderBy(history.createdAt.asc())
                .fetch()
                .stream()
                .map(ZonedDateTime::toLocalDate)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<History> findByCreatedAtAndUser(LocalDate date, Long userId) {
        ZonedDateTime startDateTime = ZonedDateTime.of(date, LocalTime.MIN, Timestamped.SEOUL_ZONE_ID);
        ZonedDateTime endDateTime = ZonedDateTime.of(date, LocalTime.MAX, Timestamped.SEOUL_ZONE_ID);

        return queryFactory.selectFrom(history)
                .where(history.createdAt.between(startDateTime, endDateTime)
                        .and(history.user.id.eq(userId)))
                .fetch();
    }

    @Override
    public boolean existsByCreatedAtAndUserAndNewsId(LocalDate date, User user, Long newsId) {
        ZonedDateTime startDateTime = ZonedDateTime.of(date, LocalTime.MIN, Timestamped.SEOUL_ZONE_ID);
        ZonedDateTime endDateTime = ZonedDateTime.of(date, LocalTime.MAX, Timestamped.SEOUL_ZONE_ID);

        BooleanExpression condition = history.createdAt.between(startDateTime, endDateTime)
                .and(history.user.eq(user))
                .and(history.newsId.eq(newsId));

        return queryFactory.selectOne()
                .from(history)
                .where(condition)
                .fetchFirst() != null;
    }
}
