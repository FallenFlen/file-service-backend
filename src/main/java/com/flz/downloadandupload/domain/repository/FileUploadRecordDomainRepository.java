package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;

import java.util.List;

public interface FileUploadRecordDomainRepository {
    void saveAll(List<FileUploadRecord> fileUploadRecords);
}
