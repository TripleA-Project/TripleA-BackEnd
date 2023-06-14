package com.triplea.triplea.dto.news;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NewsRequest {
    public static class TranslateIn {
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class WiseSTGlobal {
            private final String source_language = "en";
            private final String target_language = "ko";
            private String[] contents;
        }
        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Papago {
            private final String source = "en";
            private final String target = "ko";
            private String text;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Page {
        private int startIndex;
        private int endIndex;
        private Long nextPage;
    }
}
