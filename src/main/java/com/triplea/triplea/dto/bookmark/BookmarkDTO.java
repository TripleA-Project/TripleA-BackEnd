package com.triplea.triplea.dto.bookmark;

import lombok.Getter;
import lombok.Setter;

@Getter
public class BookmarkDTO {

    private Integer count;
    private Boolean isBookmark;

    public BookmarkDTO(Integer count, Boolean isBookmark) {
        this.count = count;
        this.isBookmark = isBookmark;
    }
}
