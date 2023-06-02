package com.triplea.triplea.dto.news;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
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

    @Getter
    @Setter
    public static class GlobalNewsDTO{

        private Long nextPage;
        private List<Data> datas;

    }


}
