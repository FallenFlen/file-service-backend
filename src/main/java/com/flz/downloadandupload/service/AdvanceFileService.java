package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.dto.request.ChunkMergeRequestDTO;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkMergeResponseDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import com.flz.downloadandupload.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvanceFileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private final FileUtils fileUtils;

    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) throws IOException {
        MultipartFile chunk = chunkUploadRequestDTO.getChunk();
        // 1.整体文件md5检查文件是否已被上传过，如果是则实现秒传
        Optional<FileUploadRecord> fileUploadRecordOptional = fileUploadRecordDomainRepository
                .findByMd5(chunkUploadRequestDTO.getFullFileMd5());
        if (fileUploadRecordOptional.isPresent()) {
            return new ChunkUploadResponseDTO(fileUploadRecordOptional
                    .map(FileUploadRecord::getMd5)
                    .get(), null, true);
        }

        // 2.分块文件上传到disk,将分块信息存入db
        FileValueObject chunkFile = fileUtils.uploadToDisk(chunkUploadRequestDTO.getFullFileName().concat("-chunk"),
                chunk.getInputStream(), StandardOpenOption.TRUNCATE_EXISTING);
        FileChunkCreateCommand command = FileChunkCreateCommand.builder()
                .number(chunkUploadRequestDTO.getNumber())
                .fullFileName(chunkUploadRequestDTO.getFullFileName())
                .fullFileMd5(chunkUploadRequestDTO.getFullFileMd5())
                .totalChunkCount(chunkUploadRequestDTO.getTotalChunkCount())
                .currentSize(chunk.getSize())
                .standardSize(chunkUploadRequestDTO.getStandardSize())
                .path(chunkFile.getPath())
                .build();
        FileChunk fileChunk = FileChunk.create(command);
        fileChunkDomainRepository.saveAll(List.of(fileChunk));

        return new ChunkUploadResponseDTO(fileChunk.getFullFileMd5(), fileChunk.getNumber(), false);
    }

    @Transactional
    public ChunkMergeResponseDTO merge(ChunkMergeRequestDTO requestDTO) {
        String fullFilePath = fileUploadRecordDomainRepository.findByMd5(requestDTO.getFullFileMd5())
                .map(FileUploadRecord::getPath)
                .orElse(null);
        if (fullFilePath != null && fileUtils.exists(fullFilePath)) {
            return new ChunkMergeResponseDTO(fullFilePath);
        }

        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5AndMerged(requestDTO.getFullFileMd5());
        if (allChunks.size() != requestDTO.getTotalChunkCount().longValue()) {
            throw new BusinessException("file merge failed,required chunk count is " +
                    requestDTO.getTotalChunkCount() + ",but existed chunk count is " + allChunks.size());
        }
        // todo 注册回滚逻辑：删除chunk文件
        FileValueObject fullFile = fileUtils.uploadToDisk(requestDTO.getFullFileName(),
                new ByteArrayInputStream(new byte[0]), StandardOpenOption.CREATE_NEW);
        allChunks.stream()
                .sorted(Comparator.comparing(FileChunk::getNumber))
                .map(FileChunk::getPath)
                .map(fileUtils::getContent)
                .forEach((content) -> fileUtils.append(fullFile.getPath(), new ByteArrayInputStream(content)));
        FileUploadRecordCreateCommand fileUploadRecordCreateCommand = FileUploadRecordCreateCommand.builder()
                .name(fullFile.getName())
                .path(fullFile.getPath())
                .size(requestDTO.getFullFileSize())
                .md5(requestDTO.getFullFileMd5())
                .build();
        FileUploadRecord fileUploadRecord = FileUploadRecord.create(fileUploadRecordCreateCommand);
        fileUploadRecordDomainRepository.saveAll(List.of(fileUploadRecord));
        fileChunkDomainRepository.deleteByFullFileMd5AndMerged(requestDTO.getFullFileMd5());
        return new ChunkMergeResponseDTO(fileUploadRecord.getPath());
    }
}
