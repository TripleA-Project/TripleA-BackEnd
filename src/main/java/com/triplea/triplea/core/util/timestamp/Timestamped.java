package com.triplea.triplea.core.util.timestamp;

import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class Timestamped extends CreatedTimestamped{
    @LastModifiedDate
    @Column(nullable = false)
    private ZonedDateTime updatedAt;
}
