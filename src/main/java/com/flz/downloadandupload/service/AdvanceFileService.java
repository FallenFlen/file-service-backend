package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.common.utils.TransactionUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.dto.request.ChunkMergeRequestDTO;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.request.FileExistenceCheckRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkMergeResponseDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import com.flz.downloadandupload.dto.response.FileExistenceResponseDTO;
import com.flz.downloadandupload.event.FileChunkDamageEvent;
import com.flz.downloadandupload.event.FileValidateEvent;
import com.flz.downloadandupload.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdvanceFileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private final FileUtils fileUtils;
    private final TransactionUtils transactionUtils;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) throws IOException {
        MultipartFile chunk = chunkUploadRequestDTO.getChunk();
        String md5 = DigestUtils.md5DigestAsHex(chunk.getInputStream());
        // 1.整体文件md5检查文件是否已被上传过，如果是则实现秒传
        if (validateAndGetFullFilePath(chunkUploadRequestDTO.getFullFileMd5()) != null) {
            return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                    true, md5, false);
        }

        // 2.by chunk md5，检测单个chunk是否被上传过，如果是则实现秒传
        Optional<FileChunk> fileChunkOptional = fileChunkDomainRepository.findByMd5(md5);
        if (fileChunkOptional.isPresent()) {
            FileChunk fileChunk = fileChunkOptional.get();
            boolean actualExisted = fileUtils.exists(fileChunk.getPath());
            boolean md5Correct = fileUtils.validateMd5(fileChunk.getMd5(), fileChunk.getPath());
            if (actualExisted && md5Correct) {
                return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                        false, md5, true);
            }
            // chunk损坏
            eventPublisher.publishEvent(new FileChunkDamageEvent(List.of(fileChunk)));
        }

        // 3.分块文件上传到disk,将分块信息存入db
        FileValueObject chunkFile = fileUtils.uploadToDisk(chunkUploadRequestDTO.getFullFileName().concat("-chunk"),
                chunk.getInputStream(), StandardOpenOption.TRUNCATE_EXISTING);
        FileChunkCreateCommand command = FileChunkCreateCommand.builder()
                .number(chunkUploadRequestDTO.getNumber())
                .fullFileName(chunkUploadRequestDTO.getFullFileName())
                .fullFileMd5(chunkUploadRequestDTO.getFullFileMd5())
                .md5(md5)
                .totalChunkCount(chunkUploadRequestDTO.getTotalChunkCount())
                .currentSize(chunk.getSize())
                .standardSize(chunkUploadRequestDTO.getStandardSize())
                .path(chunkFile.getPath())
                .build();
        FileChunk fileChunk = FileChunk.create(command);
        fileChunkDomainRepository.saveAll(List.of(fileChunk));

        return new ChunkUploadResponseDTO(fileChunk.getFullFileMd5(), false, fileChunk.getMd5(), false);
    }

    @Transactional
    public ChunkMergeResponseDTO merge(ChunkMergeRequestDTO requestDTO) {
        String fullFileActualPath = validateAndGetFullFilePath(requestDTO.getFullFileMd5());
        if (fullFileActualPath != null) {
            return new ChunkMergeResponseDTO(fullFileActualPath);
        }

        List<String> chunkPaths = validateAndGetSortedChunks(requestDTO.getFullFileMd5(), requestDTO.getTotalChunkCount());

        FileValueObject fullFile = fileUtils.uploadToDisk(requestDTO.getFullFileName(),
                new ByteArrayInputStream(new byte[0]), StandardOpenOption.CREATE_NEW);
        // 防止大文件oom
        chunkPaths.forEach((path) -> {
            byte[] content = fileUtils.getContent(path);
            fileUtils.append(fullFile.getPath(), new ByteArrayInputStream(content));
        });

        FileUploadRecordCreateCommand fileUploadRecordCreateCommand = FileUploadRecordCreateCommand.builder()
                .name(fullFile.getName())
                .path(fullFile.getPath())
                .size(requestDTO.getFullFileSize())
                .md5(requestDTO.getFullFileMd5())
                .build();
        FileUploadRecord fileUploadRecord = FileUploadRecord.create(fileUploadRecordCreateCommand);
        fileUploadRecordDomainRepository.saveAll(List.of(fileUploadRecord));
        fileChunkDomainRepository.deleteByFullFileMd5(requestDTO.getFullFileMd5());

        return new ChunkMergeResponseDTO(fileUploadRecord.getPath());
    }

    private String validateAndGetFullFilePath(String fullFileMd5) {
        return Optional.ofNullable(fileUploadRecordDomainRepository.findByMd5(fullFileMd5))
                .filter((record) -> fileUtils.exists(record.getPath()))
                .filter((record) -> fileUtils.validateMd5(record.getMd5(), record.getPath()))
                .map(FileUploadRecord::getPath)
                .orElse(null);
    }

    private List<String> validateAndGetSortedChunks(String fullFileMd5, long totalChunkCount) {
        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(fullFileMd5);
        if (allChunks.size() != totalChunkCount) {
            throw new BusinessException("file merge failed,required chunk count is " +
                    totalChunkCount + ",but existed chunk count is " + allChunks.size());
        }

        List<FileChunk> damagedChunks = getDamagedChunks(allChunks);
        if (!CollectionUtils.isEmpty(damagedChunks)) {
            transactionUtils.runAfterRollback(() -> eventPublisher.publishEvent(new FileChunkDamageEvent(damagedChunks)));
            throw new BusinessException("file chunks damaged");
        }

        return allChunks.stream()
                .sorted(Comparator.comparing(FileChunk::getNumber))
                .map(FileChunk::getPath)
                .collect(Collectors.toList());
    }

    public FileExistenceResponseDTO checkFileExistenceAndClearDamaged(FileExistenceCheckRequestDTO requestDTO) {
        String fullFileMd5 = requestDTO.getFullFileMd5();
        boolean fullFileExist = validateAndGetFullFilePath(fullFileMd5) != null;
        if (fullFileExist) {
            return new FileExistenceResponseDTO(true, Collections.emptyList());
        }

        eventPublisher.publishEvent(new FileValidateEvent(fullFileMd5));
        FileExistenceResponseDTO fileExistenceResponseDTO = new FileExistenceResponseDTO();
        fileExistenceResponseDTO.setFullFileExist(false);
        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(fullFileMd5);
        List<FileChunk> damagedChunks = getDamagedChunks(allChunks);
        if (!CollectionUtils.isEmpty(damagedChunks)) {
            eventPublisher.publishEvent(new FileChunkDamageEvent(damagedChunks));
        }

        Set<String> damagedChunkIds = damagedChunks.stream()
                .map(FileChunk::getId)
                .collect(Collectors.toSet());
        List<Integer> validChunkNumbers = allChunks.stream()
                .filter((chunk) -> !damagedChunkIds.contains(chunk.getId()))
                .map(FileChunk::getNumber)
                .distinct()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        fileExistenceResponseDTO.setValidChunkNumbers(validChunkNumbers);
        return fileExistenceResponseDTO;
    }

    private List<FileChunk> getDamagedChunks(List<FileChunk> allChunks) {
        return allChunks.stream()
                .filter((chunk) -> !fileUtils.exists(chunk.getPath()) || !fileUtils.validateMd5(chunk.getMd5(), chunk.getPath()))
                .collect(Collectors.toList());
    }

}
