package com.flz.downloadandupload.common.config;

import com.flz.downloadandupload.common.utils.IdGenerator;
import com.flz.downloadandupload.persist.dataobject.base.BaseDO;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.relational.core.mapping.event.BeforeSaveCallback;

import java.time.LocalDateTime;

@Configuration
public class JdbcConfig {

    @Bean
    public BeforeSaveCallback<BaseDO> beforeSaveCallback() {
        return (dataObject, aggregateChange) -> {
            if (dataObject.getId() == null) {
                dataObject.setId(IdGenerator.randomId());
                dataObject.setDeleted(Boolean.FALSE);
                dataObject.setCreateTime(LocalDateTime.now());
            }
            dataObject.setUpdateTime(LocalDateTime.now());
            return dataObject;
        };
    }
}
