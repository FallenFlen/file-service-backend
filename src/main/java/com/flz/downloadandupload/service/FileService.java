package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.constant.FileConstant;
import com.flz.downloadandupload.common.utils.FileUtils;
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
import com.flz.downloadandupload.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class FileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private final FileUtils fileUtils;
    private final FileUploadRecordDTOConverter converter = FileUploadRecordDTOConverter.INSTANCE;

    @Transactional
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) throws IOException {
        MultipartFile chunk = chunkUploadRequestDTO.getChunk();
        String md5 = DigestUtils.md5DigestAsHex(chunk.getInputStream());
        // 1.按整体文件md5检查文件是否已被上传过且未被损坏，如果是则实现秒传
        if (!isFullFileExistedAndValid(chunkUploadRequestDTO.getFullFileMd5())) {
            return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                    true, md5, false);
        }

        // 2.按chunk md5，检测单个chunk是否被上传过且未被损坏，如果是则实现秒传
        Optional<FileChunk> fileChunkOptional = fileChunkDomainRepository.findByMd5(md5);
        if (fileChunkOptional.isPresent()) {
            FileChunk fileChunk = fileChunkOptional.get();
            if (!isFullFileExistedAndValid(fileChunk.getMd5())) {
                return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(),
                        false, md5, true);
            }
        }

        // 3.分块文件上传到disk,将分块信息存入db
        FileValueObject chunkFile = fileUtils.uploadToDisk(
                chunkUploadRequestDTO.getFullFileName().concat(FileConstant.CHUNK_SUFFIX),
                chunk.getInputStream(),
                StandardOpenOption.TRUNCATE_EXISTING);
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
        String fullFileActualPath = getValidPathByFileMd5(requestDTO.getFullFileMd5());
        if (fullFileActualPath != null) {
            return new ChunkMergeResponseDTO(fullFileActualPath);
        }

        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(requestDTO.getFullFileMd5());

        List<String> chunkPaths = validateAndGetSortedChunks(allChunks, requestDTO.getTotalChunkCount());

        FileValueObject fullFile = fileUtils.uploadToDisk(requestDTO.getFullFileName(),
                new ByteArrayInputStream(new byte[0]), StandardOpenOption.CREATE_NEW);
        // 采用追加的方式将chunk的内容写入新文件，不一次性读取所有chunk到内存中，防止oom
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

        // 清除chunk记录和文件
        clearChunk(allChunks);

        return new ChunkMergeResponseDTO(fileUploadRecord.getPath());
    }

    @Transactional
    public FileExistenceResponseDTO checkChunkExistenceAndClearDamaged(FileExistenceCheckRequestDTO requestDTO) {
        String fullFileMd5 = requestDTO.getFullFileMd5();
        if (isFullFileExistedAndValid(fullFileMd5)) {
            return new FileExistenceResponseDTO(true, Collections.emptyList());
        }

        FileExistenceResponseDTO fileExistenceResponseDTO = new FileExistenceResponseDTO();
        fileExistenceResponseDTO.setFullFileExist(false);

        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(fullFileMd5);
        List<FileChunk> damagedChunks = getDamagedChunks(allChunks);
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

        clearChunk(damagedChunks);

        return fileExistenceResponseDTO;
    }

    public List<FileUploadRecordResponseDTO> findAll() {
        return fileUploadRecordDomainRepository.findAll().stream()
                .map(converter::toDTO)
                .collect(Collectors.toList());
    }

    private boolean isFullFileExistedAndValid(String fullFileMd5) {
        return getValidPathByFileMd5(fullFileMd5) != null;
    }

    private String getValidPathByFileMd5(String fullFileMd5) {
        return Optional.ofNullable(fileUploadRecordDomainRepository.findByMd5(fullFileMd5))
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
            throw new BusinessException("file chunks damaged");
        }

        return allChunks.stream()
                .sorted(Comparator.comparing(FileChunk::getNumber))
                .map(FileChunk::getPath)
                .collect(Collectors.toList());
    }

    private void clearChunk(List<FileChunk> chunks) {
        List<String> ids = chunks.stream()
                .map(FileChunk::getId)
                .collect(Collectors.toList());
        fileChunkDomainRepository.deleteByIds(ids);
        chunks.stream()
                .map(FileChunk::getPath)
                .forEach(fileUtils::delete);
    }

    private List<FileChunk> getDamagedChunks(List<FileChunk> allChunks) {
        return allChunks.stream()
                .filter((chunk) -> !fileUtils.validateMd5(chunk.getMd5(), chunk.getPath()))
                .collect(Collectors.toList());
    }

}
