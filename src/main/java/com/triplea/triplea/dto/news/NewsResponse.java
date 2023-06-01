package com.triplea.triplea.dto.news;

import com.triplea.triplea.dto.bookmark.BookmarkDTO;
import lombok.Getter;
import lombok.Setter;

/*
클라에대한 응답 클래스
 */
public class NewsResponse {

    @Setter @Getter
    public static class NewsDTO {
        private Long newsId;
        private String source;
        private String title;
        private String description;
        private String publishedDate;
        private BookmarkDTO bookmark;
        public NewsDTO(ApiResponse.Data data, BookmarkDTO bookmark)
        {
            this.newsId = data.getId();
            this.source = data.getSource();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.publishedDate = data.getPublishedDate();
            this.bookmark = bookmark;
        }
    }
}
