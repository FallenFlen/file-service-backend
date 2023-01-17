package com.flz.downloadandupload.persist.repository.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface FileChunkMapper {
    void deleteByIdIn(@Param("ids") List<String> ids);
}
