package com.flz.downloadandupload.domain.base;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuditAggregateRoot extends BaseEntity {
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Boolean deleted;

    public void logicDelete() {
        this.deleted = Boolean.TRUE;
    }
}
