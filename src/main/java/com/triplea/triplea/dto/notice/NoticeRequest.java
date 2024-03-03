package com.triplea.triplea.dto.notice;

import com.triplea.triplea.model.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeRequest {

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Save{
        private String title;
        private String content;

        public Notice toEntity(){
            return Notice.builder()
                    .title(title)
                    .content(content)
                    .build();
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update{
        private Long id;
        private String title;
        private String content;
    }
}
