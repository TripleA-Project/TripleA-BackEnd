package com.triplea.triplea.dto.user;

import com.triplea.triplea.model.user.User;
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

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {
        private String email;
        private String fullName;
        private Boolean newsLetter;
        private Boolean emailVerified;

        public static Detail toDTO(User user){
            return Detail.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .newsLetter(user.isNewsLetter())
                    .emailVerified(user.isEmailVerified())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Navigation{
        private String email;
        private String fullName;
        private Membership membership;

        public static Navigation toDTO(User user){
            return Navigation.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .membership(user.getMembership())
                    .build();
        }
    }
}
