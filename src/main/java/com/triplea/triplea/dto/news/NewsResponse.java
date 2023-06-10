package com.triplea.triplea.dto.news;

import com.triplea.triplea.dto.bookmark.BookmarkResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.Collections;
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
    public static class NewsDTO {
        private Long newsId;
        private String symbol;
        private String logo;
        private String source;
        private String title;
        private String description;
        private String thumbnail;
        private String publishedDate;
        private Integer sentiment;
        private BookmarkResponse.BookmarkDTO bookmark;
        public NewsDTO(ApiResponse.Data data, String logo, BookmarkResponse.BookmarkDTO bookmark)
        {
            this.newsId = data.getId();
            this.symbol = data.getSymbol();
            this.logo = logo;
            this.source = data.getSource();
            this.title = data.getTitle();
            this.description = data.getDescription();
            this.thumbnail = data.getThumbnail();
            this.publishedDate = data.getPublishedDate();
            this.sentiment = data.getSentiment();
            this.bookmark = bookmark;
        }

        public NewsDTO(ApiResponse.Details details, String logo, BookmarkResponse.BookmarkDTO bookmark) {
            this.newsId = details.getId();
            this.symbol = details.getSymbol();
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
    public static class Details{
        private Long newsId;
        private List<Symbol> symbol;
        private String source;
        private String title;
        private String description;
        private String summary;
        private String thumbnail;
        private String url;
        private String publishedDate;
        private String content;
        private Category category;
        private List keyword;
        private BookmarkResponse.BookmarkDTO bookmark;
        private Integer sentiment;

        @Builder
        public Details(ApiResponse.Details details, List<NewsResponse.Details.Symbol> symbol, String description, Category category, BookmarkResponse.BookmarkDTO bookmark) {
            this.newsId = details.getId();
            this.symbol = symbol;
            this.source = details.getSource();
            this.title = details.getTitle();
            this.description = description;
            this.summary = details.getSummary();
            this.thumbnail = details.getThumbnail();
            this.url = details.getUrl();
            this.publishedDate = details.getPublishedDate();
            this.content = details.getContent();
            this.category = category;
            if(details.getKeyword1().equals("null") && details.getKeyword2().equals("null") && details.getKeyword3().equals("null")) this.keyword = Collections.emptyList();
            else this.keyword = List.of(details.getKeyword1(), details.getKeyword2(), details.getKeyword3());
            this.bookmark = bookmark;
            this.sentiment = details.getSentiment();
        }

        @Getter
        public static class Symbol{
            private String name;
            private String companyName;
            private String sector;
            private String logo;
            private String marketType;
            private Integer buzz;
            private Integer positiveBuzz;
            private Integer negativeBuzz;
            private List<Price> price;

            public Symbol(ApiResponse.MoyaSymbol symbol, String logo, ApiResponse.MoyaBuzz buzz, List<Price> price) {
                this.name = symbol.getSymbol();
                this.companyName = symbol.getCompanyName();
                this.sector = symbol.getSector();
                this.logo = logo;
                this.marketType = symbol.getMarketType();
                this.buzz = buzz.getCount();
                this.positiveBuzz = buzz.getPositiveCount();
                this.negativeBuzz = buzz.getNegativeCount();
                this.price = price;
            }

            @Getter @Builder
            @AllArgsConstructor
            public static class Price{
                private List<ApiResponse.Tiingo> today;
                private List<ApiResponse.Tiingo> yesterday;
            }
        }

        @Getter @Builder
        @AllArgsConstructor
        public static class Category{
            private Long categoryId;
            private String category;
        }
    }
}
