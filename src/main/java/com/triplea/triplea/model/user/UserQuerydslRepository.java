package com.triplea.triplea.model.user;

import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.dto.user.UserResponse;
import com.triplea.triplea.model.history.History;

import java.time.LocalDate;
import java.util.List;

public interface UserQuerydslRepository {
    List<User> findUserByType(UserRequest.UserSearch request);
}
