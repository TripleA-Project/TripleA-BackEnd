package com.triplea.triplea.dto.news;

import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.bookmark.BookmarkResponse.BookmarkDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/*
클라에대한 응답 클래스
 */
public class NewsResponse {

    @Getter
    public static class NewsDTO {
        private Long newsId;
        private String source;
        private String title;
        private String description;
        private String publishedDate;
        private BookmarkResponse.BookmarkDTO bookmark;
        public NewsDTO(ApiResponse.Data data, BookmarkResponse.BookmarkDTO bookmark)
        {
            this.newsId = data.getId();
            this.source = data.getSource();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.publishedDate = data.getPublishedDate();
            this.bookmark = bookmark;
        }
    }

    @Setter
    @Getter
    public static class GNewsDTO {
        private Long nextPage;
        List<NewsDTO> news;
    }
}
