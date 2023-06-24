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
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String password;
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String passwordCheck;
        @NotBlank
        @Pattern(regexp = "^[a-zA-Z]*$|^[가-힣]*$", message = "한글 혹은 영문으로만 작성해주세요")
        private String fullName;
        @NotNull
        private Boolean newsLetter;
        @NotBlank
        private String emailKey;

        @AssertTrue(message = "password must be equals passwordCheck")
        private boolean isPasswordMatch() {
            if (password != null) return password.equals(passwordCheck);
            return false;
        }

        public User toEntity(String password, boolean emailVerified, String userAgent, String ipAddress, String profile) {
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
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class login {
        @Email
        @NotBlank
        private String email;
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String password;
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

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Customer {
        @NotBlank
        private String name;
        @Email
        @NotBlank
        private String email;
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Order {
        @NotNull
        private List<Item> items;
        @NotNull
        private Long customerId;
        @NotNull
        private String customerCode;

        @Getter
        @Builder
        @NoArgsConstructor
        @AllArgsConstructor
        public static class Item {
            private final String currency = "KRW";
            private final int minimumQuantity = 1;
            @NotNull
            private String productCode;
            @NotNull
            private String priceCode;
        }
    }

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Update {
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String password;
        @NotBlank
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String passwordCheck;
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String newPassword;
        @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[!@#$%^&*()\\-_=+{};:,<.>]).{8,16}$", message = "올바른 형식의 비밀번호여야 합니다")
        private String newPasswordCheck;
        @Pattern(regexp = "^[a-zA-Z]*$|^[가-힣]*$", message = "한글 혹은 영문으로만 작성해주세요")
        private String fullName;
        private Boolean newsLetter;

        @AssertTrue(message = "password must be equals passwordCheck")
        private boolean isPasswordMatch() {
            if (password != null) return password.equals(passwordCheck);
            return false;
        }

        @AssertTrue(message = "new password must be equals passwordCheck")
        private boolean isNewPasswordMatch() {
            if (newPassword != null) return newPassword.equals(newPasswordCheck);
            return false;
        }
    }
}
