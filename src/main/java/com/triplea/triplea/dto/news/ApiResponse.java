package com.triplea.triplea.dto.news;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/*
API 호출 응답을 받기 위한 클래스
 */

public class ApiResponse {

    @Getter
    @Setter
    public static class Data{
        private Long id;
        private String symbol;
        private String source;
        private String title;
        private String description;
        private String summary;
        private String thumbnail;
        private String url;
        private String publishedDate;
        private String category;
        private List<String> categoryList;
        private String keyword1;
        private String keyword2;
        private String keyword3;
        private Integer sentiment;

        @JsonSetter("category")
        public void setCategory(Object category) {
            if (category instanceof String) {
                this.category = (String) category;
                this.categoryList = new ArrayList<>();
            } else if (category instanceof List) {
                this.categoryList = (List<String>) category;
                this.category = null;
            }
        }

        @JsonGetter("category")
        public Object getCategory() {
            if (this.category != null) {
                return this.category;
            } else {
                return this.categoryList;
            }
        }
    }

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Details{
        private Long id;
        private String symbol;
        private String source;
        private String title;
        private String description;
        private String summary;
        private String thumbnail;
        private String url;
        private String publishedDate;
        private String category;
        private String content;
        private String keyword1;
        private String keyword2;
        private String keyword3;
        private Integer sentiment;
    }

    @Getter
    @Setter
    public static class GlobalNewsDTO{

        private Long nextPage;
        private List<Data> datas;

    }
    @Getter
    public static class BookmarkSymbolDTO{
        private Long id;
        private String symbol;
        private String companyName;
        private String exchange;
        private String industry;
        private String website;
        private String description;
        private String ceo;
        private String issueType;
        private String sector;
        private String logo;
        private String marketType;
    }

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tiingo{
        private String date;
        private Double open;
        private Double high;
        private Double low;
        private Double close;
        private Long volume;
        private Double adjOpen;
        private Double adjHigh;
        private Double adjLow;
        private Double adjClose;
        private Long adjVolume;
        private Double divCash;
        private Double splitFactor;

        public String getFormattedDate() {
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").withZone(ZoneId.of("UTC"));
            DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDateTime dateTime = LocalDateTime.parse(this.date, inputFormatter);
            return dateTime.format(outputFormatter);
        }
    }

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TiingoSymbol{
        private String ticker;
        private String name;
        private String description;
        private String startDate;
        private String endDate;
        private String exchangeCode;
    }
}
