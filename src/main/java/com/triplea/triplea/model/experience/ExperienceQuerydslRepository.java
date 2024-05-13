package com.triplea.triplea.model.experience;

import com.triplea.triplea.dto.experience.ExperienceRequest;
import com.triplea.triplea.dto.user.UserRequest;
import com.triplea.triplea.model.user.User;

import java.text.ParseException;
import java.util.List;

public interface ExperienceQuerydslRepository {
    List<Experience> findExperienceList(ExperienceRequest.Search search) throws ParseException;
}
