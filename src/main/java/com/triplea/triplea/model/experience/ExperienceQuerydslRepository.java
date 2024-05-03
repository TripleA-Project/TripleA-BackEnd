package com.triplea.triplea.model.experience;

import com.triplea.triplea.model.user.User;

import java.util.List;

public interface ExperienceQuerydslRepository {
    public Experience findExperienceByUser(User user, boolean type);
}
