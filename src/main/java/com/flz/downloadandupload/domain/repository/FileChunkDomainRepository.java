package com.flz.downloadandupload.domain.repository;

import com.flz.downloadandupload.domain.aggregate.FileChunk;

import java.util.List;

public interface FileChunkDomainRepository {
    void saveAll(List<FileChunk> fileChunks);
}
