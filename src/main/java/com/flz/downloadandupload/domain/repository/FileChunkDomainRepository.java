package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileChunk;

import java.util.List;

public interface FileChunkDomainRepository {
    void saveAll(List<FileChunk> fileChunks);

    FileChunk findById(String id);

    List<FileChunk> findAllByFullFileMd5AndMerged(String md5, Boolean merged);

    Integer deleteByFullFileMd5AndMerged(String md5, Boolean merged);
}
