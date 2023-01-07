package com.flz.downloadandupload.persist.converter;

import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileChunkDoConverter {
    FileChunkDoConverter INSTANCE = Mappers.getMapper(FileChunkDoConverter.class);

    FileChunkDO toDO(FileChunk fileChunk);

    FileChunk toDomain(FileChunkDO fileChunkDO);
}
