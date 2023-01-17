package com.flz.downloadandupload.domain.aggregate.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AuditAggregateRoot extends BaseEntity {
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

    public void logicDelete() {
        this.deleted = Boolean.TRUE;
    }
}
