package com.flz.downloadandupload.persist.repository.impl;

import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.exception.NotFoundException;
import com.flz.downloadandupload.persist.converter.FileChunkDoConverter;
import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import com.flz.downloadandupload.persist.repository.jdbc.FileChunkJDBCRepository;
import com.flz.downloadandupload.persist.repository.mapper.FileChunkMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class FileChunkDomainRepositoryImpl implements FileChunkDomainRepository {
    private final FileChunkJDBCRepository fileChunkJDBCRepository;
    private final FileChunkMapper fileChunkMapper;
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

    @Override
    public List<FileChunk> findAllByFullFileMd5AndMerged(String md5) {
        List<FileChunkDO> allByFullFileMd5AndDeletedIsFalse = fileChunkJDBCRepository.findAllByFullFileMd5AndDeletedIsFalse(md5);
        return allByFullFileMd5AndDeletedIsFalse.stream()
                .map(converter::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByFullFileMd5AndMerged(String md5) {
        fileChunkJDBCRepository.deleteAllByFullFileMd5(md5);
    }

    @Override
    public void deleteByIds(List<String> ids) {
        fileChunkMapper.deleteByIdIn(ids);
    }

    @Override
    public Optional<FileChunk> findByMd5(String md5) {
        return fileChunkJDBCRepository.findFirstByMd5AndDeletedIsFalse(md5)
                .map(converter::toDomain);
    }
}
