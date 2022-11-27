package com.flz.downloadandupload.persist.converter;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileUploadRecordDOConverter {
    FileUploadRecordDOConverter INSTANCE = Mappers.getMapper(FileUploadRecordDOConverter.class);

    FileUploadRecordDO toDO(FileUploadRecord fileUploadRecord);
}
