package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.enums.FileStatus;

import java.util.List;

public interface FileUploadRecordDomainRepository extends FileRepository<FileUploadRecord> {
    void saveAll(List<FileUploadRecord> fileUploadRecords);

    FileUploadRecord findByPath(String path);

    FileUploadRecord findByMd5(String md5);

    void deleteById(String id);

    void deleteByIds(List<String> ids);

    List<FileUploadRecord> findAll();

    List<FileUploadRecord> findAllByStatusNotEqualAndLimit(FileStatus status, int limit);
}
