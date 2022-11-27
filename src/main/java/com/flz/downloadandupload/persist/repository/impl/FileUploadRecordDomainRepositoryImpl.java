package com.flz.downloadandupload.persist.repository.impl;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.persist.converter.FileUploadRecordDOConverter;
import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import com.flz.downloadandupload.persist.repository.jdbc.FileUploadRecordJDBCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileUploadRecordDomainRepositoryImpl implements FileUploadRecordDomainRepository {
    private final FileUploadRecordJDBCRepository jdbcRepository;
    private final FileUploadRecordDOConverter converter = FileUploadRecordDOConverter.INSTANCE;

    @Override
    public void saveAll(List<FileUploadRecord> fileUploadRecords) {
        List<FileUploadRecordDO> fileUploadRecordDOS = fileUploadRecords.stream()
                .map(converter::toDO)
                .collect(Collectors.toList());
        jdbcRepository.saveAll(fileUploadRecordDOS);
    }
}