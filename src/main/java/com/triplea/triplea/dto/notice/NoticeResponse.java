package com.triplea.triplea.dto.notice;

import com.triplea.triplea.model.notice.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class NoticeResponse {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public class Notice{
        private Long id;
        private String title;
        private String content;

        public Notice (Notice notice){
            this.id = notice.getId();
            this.title = notice.getTitle();
            this.content = notice.getContent();
        }
    }
}
