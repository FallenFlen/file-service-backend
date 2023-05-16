package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.enums.FileStatus;

import java.util.List;
import java.util.Optional;

public interface FileChunkDomainRepository extends FileRepository<FileChunk> {
    void saveAll(List<FileChunk> fileChunks);

    FileChunk findById(String id);

    List<FileChunk> findAllByFullFileMd5(String md5);

    void deleteByFullFileMd5(String md5);

    void deleteByIds(List<String> ids);

    Optional<FileChunk> findByMd5(String md5);

    List<FileChunk> findAllByStatusNotEqualAndLimit(FileStatus status, int limit);
}
