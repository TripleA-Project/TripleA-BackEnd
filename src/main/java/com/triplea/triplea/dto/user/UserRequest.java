package com.triplea.triplea.dto.user;

import com.triplea.triplea.model.user.User;
import lombok.*;

import javax.validation.constraints.*;

@Getter
public class UserRequest {
    @Getter @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Join{
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
        private boolean isPasswordMatch(){
            if(password != null) return password.equals(passwordCheck);
            return false;
        }

        public User toEntity(String password, String userAgent, String ipAddress, String profile){
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
}
