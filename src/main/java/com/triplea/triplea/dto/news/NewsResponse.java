package com.triplea.triplea.dto.news;

import com.triplea.triplea.core.util.LogoUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import java.util.List;



/*
클라에대한 응답 클래스
 */
public class NewsResponse {

    @Builder @Getter
    @AllArgsConstructor
    public static class News{
        private String search;
        private Long nextPage;
        private List<NewsDTO> news;
    }

    @Getter
    public static class BookmarkDTO {

        private Integer count;
        private Boolean isBookmark;

        @Builder
        public BookmarkDTO(Integer count, Boolean isBookmark) {
            this.count = count;
            this.isBookmark = isBookmark;
        }
    }

    @Getter
    public static class NewsDTO {
        private Long newsId;
        private String symbol;
        private String logo;
        private String source;
        private String title;
        private String description;
        private String thumbnail;
        private Integer sentiment;
        private String publishedDate;
        private BookmarkDTO bookmark;
        public NewsDTO(ApiResponse.Data data, BookmarkDTO bookmark)
        {
            this.newsId = data.getId();
            this.symbol = data.getSymbol();
            this.logo = LogoUtil.makeLogo(data.getSymbol());
            this.source = data.getSource();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.thumbnail = data.getThumbnail();
            this.publishedDate = data.getPublishedDate();
            this.sentiment = data.getSentiment();
            this.bookmark = bookmark;
        }

        public NewsDTO(ApiResponse.Details details, BookmarkDTO bookmark) {
            this.newsId = details.getId();
            this.source = details.getSource();
            this.title = details.getTitle();
            this.description = details.getDescription();
            this.publishedDate = details.getPublishedDate();
            this.bookmark = bookmark;
        }
    }
}
