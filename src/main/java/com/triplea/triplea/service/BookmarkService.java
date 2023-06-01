package com.triplea.triplea.service;

import com.triplea.triplea.model.bookmark.Bookmark;
import com.triplea.triplea.model.bookmark.BookmarkRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public boolean insertBookmark(Long newsid, User user) {

        try {
            Bookmark bookmark = Bookmark.builder()
                    .newsId(newsid)
                    .user(user)
                    .isDeleted(false)
                    .build();

            bookmarkRepository.save(bookmark);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean deleteBookmark(Long newsid, User user) {

        try{
            Optional<Bookmark> bookmarkPS = bookmarkRepository.findByNewsIdAndUser(newsid, user);
            bookmarkPS.get().setDeleted(true);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
