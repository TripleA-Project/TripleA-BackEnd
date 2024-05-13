package com.triplea.triplea.dto.user;

import com.querydsl.core.annotations.QueryProjection;
import com.triplea.triplea.model.experience.Experience;
import com.triplea.triplea.model.user.User;
import com.triplea.triplea.model.user.User.Membership;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

public class UserResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment {
        private URL payment;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Session {
        private String session;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PaymentDate {
        private String paymentDate;
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

        public static Detail toDTO(User user) {
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
    public static class Navigation {
        private String email;
        private String fullName;
        private Membership membership;
        private User.MemberRole memberRole;
        private String nextPaymentDate;
        private boolean freeTrial;
        private Date freeTierStartDate;
        private Date freeTierEndDate;

        public static Navigation toDTO(User user,String nextPaymentDate, boolean freeTrial, Experience experience) {
            return Navigation.builder()
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .membership(user.getMembership())
                    .memberRole(user.getMemberRole())
                    .nextPaymentDate(nextPaymentDate)
                    .freeTrial(freeTrial)
                    .freeTierStartDate(experience.getStartDate())
                    .freeTierEndDate(experience.getEndDate())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserInfo {
        private Long id;
        private ZonedDateTime createAt;
        private String email;
        private String fullName;
        private boolean newLetter;
        private Membership membership;
        private User.MemberRole memberRole;
        private String changeMembershipDate;

        public static UserInfo toDTO(User user) {
            return UserInfo.builder()
                    .id(user.getId())
                    .createAt(user.getCreatedAt())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .newLetter(user.isNewsLetter())
                    .membership(user.getMembership())
                    .memberRole(user.getMemberRole())
                    .changeMembershipDate(user.getNextPaymentDate())
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserListLength {
        private int totalUserLength;

        private int basicUserLength;

        private int premiumLength;

        public static UserListLength toDTO(int totalUserLength, int basicUserLength, int premiumLength) {
            return UserListLength.builder()
                    .totalUserLength(totalUserLength)
                    .basicUserLength(basicUserLength)
                    .premiumLength(premiumLength)
                    .build();
        }
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UserFreeTierInfo {
        private Long id;
        private String email;
        private String fullName;
        private boolean freeTier;
        private Date freeTierStartDate;
        private Date freeTierEndDate;
        private String memo;
        public static UserFreeTierInfo toDTO(User user,boolean freeTier, Experience experience) {
            return UserFreeTierInfo.builder()
                    .id(user.getId())
                    .email(user.getEmail())
                    .fullName(user.getFullName())
                    .freeTier(freeTier)
                    .freeTierStartDate(experience.getStartDate())
                    .freeTierEndDate(experience.getEndDate())
                    .memo(experience.getMemo())
                    .build();
        }
    }
}
