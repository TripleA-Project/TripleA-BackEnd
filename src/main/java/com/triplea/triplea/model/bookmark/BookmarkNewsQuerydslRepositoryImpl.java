package com.triplea.triplea.model.bookmark;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.triplea.triplea.core.util.timestamp.Timestamped;
import org.springframework.stereotype.Repository;
import com.triplea.triplea.model.bookmark.QBookmarkNews;
import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.List;

@Repository
public class BookmarkNewsQuerydslRepositoryImpl implements BookmarkNewsQuerydslRepository{
    private final JPAQueryFactory queryFactory;
    private final QBookmarkNews bookmarkNews = QBookmarkNews.bookmarkNews;

    public BookmarkNewsQuerydslRepositoryImpl(EntityManager entityManager) {
        this.queryFactory = new JPAQueryFactory(entityManager);
    }
    @Override
    public List<BookmarkNews> findByCreatedAtAndUser(LocalDate date, Long userId) {
        ZonedDateTime startDateTime = ZonedDateTime.of(date, LocalTime.MIN, Timestamped.SEOUL_ZONE_ID);
        ZonedDateTime endDateTime = ZonedDateTime.of(date, LocalTime.MAX, Timestamped.SEOUL_ZONE_ID);

        return queryFactory.selectFrom(bookmarkNews)
                .where(bookmarkNews.createdAt.between(startDateTime, endDateTime)
                        .and(bookmarkNews.user.id.eq(userId)))
                .fetch();
    }
}
