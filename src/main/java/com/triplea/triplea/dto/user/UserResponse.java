package com.triplea.triplea.dto.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.net.URL;

public class UserResponse {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Payment{
        private URL payment;
    }
}
