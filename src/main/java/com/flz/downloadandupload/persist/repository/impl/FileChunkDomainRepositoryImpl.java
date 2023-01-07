package com.flz.downloadandupload.persist.repository.impl;

import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.exception.NotFoundException;
import com.flz.downloadandupload.persist.converter.FileChunkDoConverter;
import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import com.flz.downloadandupload.persist.repository.jdbc.FileChunkJDBCRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileChunkDomainRepositoryImpl implements FileChunkDomainRepository {
    private final FileChunkJDBCRepository fileChunkJDBCRepository;
    private final FileChunkDoConverter converter = FileChunkDoConverter.INSTANCE;

    @Override
    public void saveAll(List<FileChunk> fileChunks) {
        List<FileChunkDO> fileChunkDOS = fileChunks.stream()
                .map(converter::toDO)
                .collect(Collectors.toList());
        fileChunkJDBCRepository.saveAll(fileChunkDOS);
    }

    @Override
    public FileChunk findById(String id) {
        return fileChunkJDBCRepository.findById(id)
                .map(converter::toDomain)
                .orElseThrow(() -> new NotFoundException("file chunk not found with id " + id));
    }
}
