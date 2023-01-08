package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.ByteUtils;
import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdvanceFileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private final FileUtils fileUtils;

    @Transactional
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) throws IOException {
        MultipartFile chunk = chunkUploadRequestDTO.getChunk();
//      1.整体文件md5检查文件是否已被上传过，如果是则实现秒传
        Optional<FileUploadRecord> fileUploadRecordOptional = fileUploadRecordDomainRepository
                .findByMd5(chunkUploadRequestDTO.getFullFileMd5());
        if (fileUploadRecordOptional.isPresent()) {
            return new ChunkUploadResponseDTO(fileUploadRecordOptional
                    .map(FileUploadRecord::getMd5)
                    .get(), null, true, true);
        }

        // 2.分块文件上传到disk,将分块信息存入db
        FileValueObject chunkFile = fileUtils.uploadToDisk(chunkUploadRequestDTO.getFullFileName().concat("-chunk"), chunk.getInputStream());
        FileChunkCreateCommand command = FileChunkCreateCommand.builder()
                .number(chunkUploadRequestDTO.getNumber())
                .fullFileName(chunkUploadRequestDTO.getFullFileName())
                .fullFileMd5(chunkUploadRequestDTO.getFullFileMd5())
                .totalChunkCount(chunkUploadRequestDTO.getTotalChunkCount())
                .currentSize(chunkUploadRequestDTO.getCurrentSize())
                .standardSize(chunkUploadRequestDTO.getStandardSize())
                .path(chunkFile.getPath())
                .build();
        FileChunk fileChunk = FileChunk.create(command);
        fileChunkDomainRepository.saveAll(List.of(fileChunk));

        // 3.文件合并
        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5AndMerged(chunkUploadRequestDTO.getFullFileMd5(), false);
        if (allChunks.size() != chunkUploadRequestDTO.getTotalChunkCount().longValue()) {
            return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(), chunkUploadRequestDTO.getNumber(), false, false);
        }
        List<FileChunk> sortedAndMergedChunks = allChunks.stream()
                .sorted(Comparator.comparing(FileChunk::getNumber))
                .peek(FileChunk::merge)
                .collect(Collectors.toList());
        List<byte[]> byteArrays = sortedAndMergedChunks.stream()
                .map(FileChunk::getPath)
                .map(fileUtils::getContent)
                .collect(Collectors.toList());
        byte[] content = ByteUtils.merge(byteArrays);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(content);
        FileValueObject fullFile = fileUtils.uploadToDisk(chunkUploadRequestDTO.getFullFileName(), inputStream);
        FileUploadRecordCreateCommand fileUploadRecordCreateCommand = FileUploadRecordCreateCommand.builder()
                .name(fullFile.getName())
                .path(fullFile.getPath())
                .size((long) content.length)
                .md5(DigestUtils.md5DigestAsHex(inputStream))
                .build();
        FileUploadRecord fileUploadRecord = FileUploadRecord.create(fileUploadRecordCreateCommand);
        fileUploadRecordDomainRepository.saveAll(List.of(fileUploadRecord));
        fileChunkDomainRepository.saveAll(sortedAndMergedChunks);
        return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(), chunkUploadRequestDTO.getNumber(), false, true);
    }
}
