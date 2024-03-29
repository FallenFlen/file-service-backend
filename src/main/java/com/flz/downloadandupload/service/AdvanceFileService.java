package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.common.utils.ResponseUtils;
import com.flz.downloadandupload.common.utils.TransactionUtils;
import com.flz.downloadandupload.converter.FileUploadRecordDTOConverter;
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
import com.flz.downloadandupload.dto.response.FileUploadRecordResponseDTO;
import com.flz.downloadandupload.event.FileChunkDamageEvent;
import com.flz.downloadandupload.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
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
    private final FileUploadRecordDTOConverter converter = FileUploadRecordDTOConverter.INSTANCE;

    @Transactional
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) throws IOException {
        MultipartFile chunk = chunkUploadRequestDTO.getChunk();
        String md5 = DigestUtils.md5DigestAsHex(chunk.getInputStream());
        // 1.整体文件md5检查文件是否已被上传过，如果是则实现秒传
        if (getActuallyExistedFullFilePath(chunkUploadRequestDTO.getFullFileMd5()) != null) {
            return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                    true, md5, false);
        }

        // 2.by chunk md5，检测单个chunk是否被上传过，如果是则实现秒传
        Optional<FileChunk> fileChunkOptional = fileChunkDomainRepository.findByMd5(md5);
        if (fileChunkOptional.isPresent()) {
            FileChunk fileChunk = fileChunkOptional.get();
            List<FileChunk> damagedChunks = getDamagedChunks(List.of(fileChunk));
            if (!CollectionUtils.isEmpty(damagedChunks)) {
                return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                        false, md5, true);
            }
            // chunk损坏，清除
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
        String fullFileActualPath = getActuallyExistedFullFilePath(requestDTO.getFullFileMd5());
        if (fullFileActualPath != null) {
            return new ChunkMergeResponseDTO(fullFileActualPath);
        }

        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(requestDTO.getFullFileMd5());

        List<String> chunkPaths = validateAndGetSortedChunks(allChunks, requestDTO.getTotalChunkCount());

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

        allChunks.stream()
                .map(FileChunk::getPath)
                .filter(fileUtils::exists)
                .forEach(fileUtils::delete);
        fileChunkDomainRepository.deleteByFullFileMd5(requestDTO.getFullFileMd5());

        return new ChunkMergeResponseDTO(fileUploadRecord.getPath());
    }

    private String getActuallyExistedFullFilePath(String fullFileMd5) {
        return Optional.ofNullable(fileUploadRecordDomainRepository.findByMd5(fullFileMd5))
                .filter((record) -> fileUtils.exists(record.getPath()))
                .filter((record) -> fileUtils.validateMd5(record.getMd5(), record.getPath()))
                .map(FileUploadRecord::getPath)
                .orElse(null);
    }

    private List<String> validateAndGetSortedChunks(List<FileChunk> allChunks, long totalChunkCount) {
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
        boolean fullFileExist = getActuallyExistedFullFilePath(fullFileMd5) != null;
        if (fullFileExist) {
            return new FileExistenceResponseDTO(true, Collections.emptyList());
        }

        validateOrElseCleanFileUploadRecord(fullFileMd5);

        FileExistenceResponseDTO fileExistenceResponseDTO = new FileExistenceResponseDTO();
        fileExistenceResponseDTO.setFullFileExist(false);

        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(fullFileMd5);
        Set<String> damagedChunkIds = validateOrElseCleanDamagedChunks(allChunks);
        List<Integer> validChunkNumbers = allChunks.stream()
                .filter((chunk) -> !damagedChunkIds.contains(chunk.getId()))
                .map(FileChunk::getNumber)
                .distinct()
                .sorted(Integer::compareTo)
                .collect(Collectors.toList());
        fileExistenceResponseDTO.setValidChunkNumbers(validChunkNumbers);

        return fileExistenceResponseDTO;
    }

    private Set<String> validateOrElseCleanDamagedChunks(List<FileChunk> allChunks) {
        List<FileChunk> damagedChunks = getDamagedChunks(allChunks);
        if (!CollectionUtils.isEmpty(damagedChunks)) {
            eventPublisher.publishEvent(new FileChunkDamageEvent(damagedChunks));
        }

        return damagedChunks.stream()
                .map(FileChunk::getId)
                .collect(Collectors.toSet());
    }

    private void validateOrElseCleanFileUploadRecord(String fullFileMd5) {
        Optional.ofNullable(fileUploadRecordDomainRepository.findByMd5(fullFileMd5))
                .ifPresent((record) -> {
                    String path = record.getPath();
                    boolean exists = fileUtils.exists(path);
                    if (exists) {
                        boolean md5Correct = fileUtils.validateMd5(record.getMd5(), path);
                        if (!md5Correct) {
                            fileUtils.delete(path);
                            fileUploadRecordDomainRepository.deleteById(record.getId());
                        }
                    } else {
                        fileUploadRecordDomainRepository.deleteById(record.getId());
                    }
                });
    }

    private List<FileChunk> getDamagedChunks(List<FileChunk> allChunks) {
        return allChunks.stream()
                .filter((chunk) -> !fileUtils.exists(chunk.getPath()) || !fileUtils.validateMd5(chunk.getMd5(), chunk.getPath()))
                .collect(Collectors.toList());
    }

    public void download(String path, HttpServletResponse response) throws IOException {
        FileUploadRecord fileUploadRecord = fileUploadRecordDomainRepository.findByPath(path);
        ResponseUtils.downloadWithChunk(response, fileUploadRecord.getName(), fileUploadRecord.getPath(), fileUploadRecord.getSize());
    }

    public List<FileUploadRecordResponseDTO> findAll() {
        return fileUploadRecordDomainRepository.findAll().stream()
                .map(converter::toDTO)
                .collect(Collectors.toList());
    }
}
