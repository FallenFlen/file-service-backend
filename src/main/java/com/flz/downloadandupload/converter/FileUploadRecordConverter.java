package com.flz.downloadandupload.converter;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileUploadRecordConverter {
    FileUploadRecordConverter INSTANCE = Mappers.getMapper(FileUploadRecordConverter.class);

    FileUploadRecordDO toDO(FileUploadRecord fileUploadRecord);
}
