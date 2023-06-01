package com.triplea.triplea.service;

import com.triplea.triplea.model.bookmark.Bookmark;
import com.triplea.triplea.model.bookmark.BookmarkRepository;
import com.triplea.triplea.model.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class BookmarkService {

    private final BookmarkRepository bookmarkRepository;

    @Transactional
    public boolean 북마크추가(Long newsid, User user) {

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
    public boolean 북마크삭제(Long newsid, User user) {

        try{
            bookmarkRepository.deleteByNewsIdAndUser(newsid, user);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
