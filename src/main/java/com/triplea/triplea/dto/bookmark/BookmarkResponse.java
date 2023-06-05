package com.triplea.triplea.dto.bookmark;

import lombok.Getter;

public class BookmarkResponse {
    @Getter
    public static class BookmarkDTO {

        private Integer count;
        private Boolean isBookmark;

        public BookmarkDTO(Integer count, Boolean isBookmark) {
            this.count = count;
            this.isBookmark = isBookmark;
        }
    }
}