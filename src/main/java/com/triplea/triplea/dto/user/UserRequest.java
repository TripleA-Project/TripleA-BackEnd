package com.triplea.triplea.dto.user;

import com.triplea.triplea.model.user.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.util.List;

@Getter
public class UserRequest {
    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Join {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9.-]{6,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String password;
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z0-9.-]{6,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String passwordCheck;
        @NotBlank
        private String fullName;
        @NotNull
        private Boolean newsLetter;
        @NotNull
        private Boolean emailVerified;

        @AssertTrue(message = "password must be equals passwordCheck")
        private boolean isPasswordMatch() {
            if (password != null) return password.equals(passwordCheck);
            return false;
        }

        public User toEntity(String password, String userAgent, String ipAddress, String profile) {
            return User.builder()
                    .email(email)
                    .password(password)
                    .fullName(fullName)
                    .newsLetter(newsLetter)
                    .emailVerified(emailVerified)
                    .userAgent(userAgent)
                    .clientIP(ipAddress)
                    .profile(profile)
                    .build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailSend {
        @Email
        @NotBlank
        private String email;
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmailVerify {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        private String code;
    }

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer{
        @NotBlank
        private String name;
        @Email
        @NotBlank
        private String email;
    }

    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order{
        @NotNull
        private List<Item> items;
        @NotNull
        private Long customerId;
        @NotNull
        private String customerCode;

        @Getter @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item{
            private final String currency = "KRW";
            private final int minimumQuantity = 1;
            @NotNull
            private String productCode;
            @NotNull
            private String priceCode;
        }
    }
}
