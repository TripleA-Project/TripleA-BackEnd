package com.triplea.triplea.core.util;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.ZonedDateTime;

@Getter
public abstract class Timestamped extends CreatedTimestamped{
    @LastModifiedDate
    private ZonedDateTime updatedAt;
}
