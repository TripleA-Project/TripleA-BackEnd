package com.triplea.triplea.dto.news;

import lombok.*;

import java.time.LocalDate;
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
        private String publishedDate;  // modified
        private List<String> category;  // modified
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

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoyaSymbol{
        private Long id;
        private String symbol;
        private String companyName;
        private String exchange;
        private String industry;
        private String website;
        private String description;
        private String CEO;
        private String issueType;
        private String sector;
        private String logo;
        private String marketType;
    }

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MoyaBuzz{
        private Float sentiment;
        private Integer count;
        private Integer positiveCount;
        private Integer negativeCount;
        private LocalDate publishedDate;
    }

    @Builder @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tiingo{
        private LocalDate date;
        private Float open;
        private Float high;
        private Float low;
        private Float close;
        private Long volume;
        private Float adjOpen;
        private Float adjHigh;
        private Float adjLow;
        private Float adjClose;
        private Long adjVolume;
        private Float divCash;
        private Float splitFactor;
    }
}
