package com.triplea.triplea.dto.news;

import com.triplea.triplea.core.util.LogoUtil;
import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import com.triplea.triplea.dto.category.CategoryResponse;
import com.triplea.triplea.dto.symbol.SymbolResponse;
import com.triplea.triplea.dto.user.UserResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*
클라에대한 응답 클래스
 */
public class NewsResponse {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class News{
        private String search;
        private Long nextPage;
        private List<NewsDTO> news;
    }

    @Getter
    public static class NewsDTO {
        private Long newsId;
        private String symbol;
        private String logo;
        private String companyName;
        private String source;
        private String title;
        private String description;
        private String thumbnail;
        private String publishedDate;
        private Integer sentiment;
        private BookmarkResponse.BookmarkDTO bookmark;

        public NewsDTO(ApiResponse.Data data, String companyName ,BookmarkResponse.BookmarkDTO bookmark)
        {
            this.newsId = data.getId();
            this.symbol = data.getSymbol();
            this.logo = LogoUtil.makeLogo(data.getSymbol());
            this.companyName = companyName;
            this.source = data.getSource();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.thumbnail = data.getThumbnail();
            this.publishedDate = data.getPublishedDate();
            this.sentiment = data.getSentiment();
            this.bookmark = bookmark;
        }
        @Builder
        public NewsDTO(ApiResponse.Details details, String companyName, String logo, BookmarkResponse.BookmarkDTO bookmark) {
            this.newsId = details.getId();
            this.symbol = details.getSymbol();
            this.companyName = companyName;
            this.logo = logo;
            this.source = details.getSource();
            this.title = details.getTitle();
            this.description = details.getDescription();
            this.thumbnail = details.getThumbnail();
            this.publishedDate = details.getPublishedDate();
            this.sentiment = details.getSentiment();
            this.bookmark = bookmark;
        }
    }

    @Getter
    public static class Details {
        private UserResponse.News user;
        private Long newsId;
        private SymbolResponse.News symbol;
        private String source;
        private String thumbnail;
        private String url;
        private String publishedDate;
        private CategoryResponse category;
        private List<String> keyword = new ArrayList<>();
        private Article eng;
        private Article kor;
        private BookmarkResponse.BookmarkDTO bookmark;
        private Integer sentiment;

        @Builder
        public Details(UserResponse.News user, SymbolResponse.News symbol, ApiResponse.Details details, CategoryResponse category, Article eng, Article kor, BookmarkResponse.BookmarkDTO bookmark) {
            this.user = user;
            this.newsId = details.getId();
            this.symbol = symbol;
            this.source = details.getSource();
            this.thumbnail = details.getThumbnail();
            this.url = details.getUrl();
            this.publishedDate = details.getPublishedDate();
            this.category = category;
            if (details.getKeyword1() == null && details.getKeyword2() == null && details.getKeyword3() == null)
                this.keyword = Collections.emptyList();
            else {
                this.keyword.add(details.getKeyword1());
                this.keyword.add(details.getKeyword2());
                this.keyword.add(details.getKeyword3());
            }
            this.eng = eng;
            this.kor = kor;
            this.bookmark = bookmark;
            this.sentiment = details.getSentiment();
        }

        @Getter
        public static class Article{
            private String title;
            private String description;
            private String summary;
            private String content;

            public Article(String title) {
                this.title = title;
            }
            public Article(ApiResponse.Details details){
                this.title = details.getTitle();
                this.description = details.getDescription();
                this.summary = details.getSummary();
                this.content = details.getContent();
            }
            public Article(NewsResponse.TranslateOut translate){
                this.title = translate.getTitle();
                this.description = translate.getDescription();
                this.summary = translate.getSummary();
                this.content = translate.getContent();
            }
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateOut {
        private String title;
        private String description;
        private String summary;
        private String content;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Article{
            NewsResponse.Details.Article articleEng;
            NewsResponse.Details.Article articleKor;
        }
    }
}
