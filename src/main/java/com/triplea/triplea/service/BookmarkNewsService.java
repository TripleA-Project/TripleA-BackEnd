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
public class BookmarkNewsService {

    private final BookmarkNewsRepository bookmarkNewsRepository;

    @Transactional
    public void insertBookmark(Long newsid, User user) {
        Optional<BookmarkNews> nonDeletedByNewsIdAndUserId = bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(newsid, user.getId());
        if(nonDeletedByNewsIdAndUserId.isPresent()){
            log.error("Attempted to add a bookmark that already exists. newsId: " + newsid + ", user: " + user.getEmail());
            throw new Exception400("BookmarkNews", "news ID " + newsid + " already exists");
        }
        BookmarkNews bookmarkNews = BookmarkNews.builder()
                .newsId(newsid)
                .user(user)
                .isDeleted(false)
                .build();
        try {
            bookmarkNewsRepository.save(bookmarkNews);
        } catch (DataAccessException  e) {
            log.error("Database error when inserting bookmark", e);
            throw new Exception500("Database error");
        }
    }
    @Transactional
    public void deleteBookmark(Long newsid, User user) {
        try{
            Optional<BookmarkNews> bookmarkPS = bookmarkNewsRepository.findNonDeletedByNewsIdAndUserId(newsid, user.getId());
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
