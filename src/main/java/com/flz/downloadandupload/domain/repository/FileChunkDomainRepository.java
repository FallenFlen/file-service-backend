package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileChunk;

import java.util.List;
import java.util.Optional;

public interface FileChunkDomainRepository {
    void saveAll(List<FileChunk> fileChunks);

    FileChunk findById(String id);

    List<FileChunk> findAllByFullFileMd5AndMerged(String md5);

    void deleteByFullFileMd5AndMerged(String md5);

    void deleteByIds(List<String> ids);

    Optional<FileChunk> findByMd5(String md5);
}
