package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;

import java.util.List;
import java.util.Optional;

public interface FileUploadRecordDomainRepository {
    void saveAll(List<FileUploadRecord> fileUploadRecords);

    FileUploadRecord findByPath(String path);

    Optional<FileUploadRecord> findByMd5(String md5);
}
