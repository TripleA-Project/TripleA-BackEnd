package com.triplea.triplea.dto.user;

import com.triplea.triplea.model.user.User.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.util.List;

public class UserResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment{
        private URL payment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Session{
        private String session;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    public static class News {
        private Membership membership;
        private Integer leftBenefitCount;
        private List<Long> historyNewsIds;
    }
}
