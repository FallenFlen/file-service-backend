package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;

import java.util.List;

public interface FileUploadRecordDomainRepository {
    void saveAll(List<FileUploadRecord> fileUploadRecords);

    FileUploadRecord findByPath(String path);

    FileUploadRecord findByMd5(String md5);

    void deleteById(String id);
}
