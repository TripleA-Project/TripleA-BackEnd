package com.triplea.triplea.dto.news;

import lombok.*;
import org.springframework.scheduling.support.SimpleTriggerContext;

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
        private List<String> category;
        private String keyword1;
        private String keyword2;
        private String keyword3;
        private Integer sentiment;
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
    }
}
