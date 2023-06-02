package com.triplea.triplea.service;

import com.triplea.triplea.core.exception.Exception400;
import com.triplea.triplea.core.exception.Exception500;
import com.triplea.triplea.model.bookmark.BookmarkNews;
import com.triplea.triplea.model.bookmark.BookmarkNewsRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkNewsRepository bookmarkNewsRepository;

    @Transactional
    public void insertBookmark(Long newsid, User user) {

        try {
            BookmarkNews bookmarkNews = BookmarkNews.builder()
                    .newsId(newsid)
                    .user(user)
                    .isDeleted(false)
                    .build();

            BookmarkNews savedBookmarkNews = bookmarkNewsRepository.save(bookmarkNews);

            if(savedBookmarkNews == null || savedBookmarkNews.getId() == null) {
                log.error("Database error when inserting bookmark: Save operation returned null");
                throw new Exception500("Database error");
            }
        } catch (DataAccessException  e) {
            log.error("Database error when inserting bookmark", e);
            throw new Exception500("Database error");
        }
    }

    @Transactional
    public void deleteBookmark(Long newsid, User user) {

        try{
            Optional<BookmarkNews> bookmarkPS = bookmarkNewsRepository.findByNewsIdAndUser(newsid, user);
            if(false == bookmarkPS.isPresent()) {
                log.error("Bookmark not found for news {} and user {}", newsid, user);
                throw new Exception400("bookmark", "Bookmark not found");
            }

                bookmarkPS.get().deleteBookmark();

        }catch(DataAccessException e){
            log.error("Database error when deleting bookmark", e);
            throw new Exception500("Database error");
        }
    }
}
