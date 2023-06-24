package com.triplea.triplea.core.util.timestamp;

import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class CreatedTimestamped {
    public static final ZoneId SEOUL_ZONE_ID = ZoneId.of("Asia/Seoul");
    public static final ZoneId EST_ZONE_ID = ZoneId.of("America/New_York");
    @CreatedDate
    @Column(nullable = false)
    private ZonedDateTime createdAt;
}
