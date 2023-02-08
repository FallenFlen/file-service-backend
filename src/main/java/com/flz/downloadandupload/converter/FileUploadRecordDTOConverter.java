package com.flz.downloadandupload.converter;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.dto.response.FileUploadRecordResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FileUploadRecordDTOConverter {
    FileUploadRecordDTOConverter INSTANCE = Mappers.getMapper(FileUploadRecordDTOConverter.class);

    FileUploadRecordResponseDTO toDTO(FileUploadRecord fileUploadRecord);
}
