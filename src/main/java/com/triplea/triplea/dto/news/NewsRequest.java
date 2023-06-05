package com.triplea.triplea.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NewsRequest {
    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TranslateIn{
        private final String source_language = "en";
        private final String target_language = "ko";
        private String[] contents;
    }
}
