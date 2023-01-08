package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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
        // 2.分块文件上传到disk
        FileValueObject fileValueObject = fileUtils.commonUploadToDisk(chunkUploadRequestDTO.getFullFileName().concat("-chunk"), chunk.getInputStream());

        //      3.将分块信息存入db
        FileChunkCreateCommand command = FileChunkCreateCommand.builder()
                .number(chunkUploadRequestDTO.getNumber())
                .fullFileName(chunkUploadRequestDTO.getFullFileName())
                .fullFileMd5(chunkUploadRequestDTO.getFullFileMd5())
                .totalChunkCount(chunkUploadRequestDTO.getTotalChunkCount())
                .currentSize(chunkUploadRequestDTO.getCurrentSize())
                .standardSize(chunkUploadRequestDTO.getStandardSize())
                .path(fileValueObject.getPath())
                .build();
        FileChunk fileChunk = FileChunk.create(command);
        fileChunkDomainRepository.saveAll(List.of(fileChunk));

        // 4.文件合并
        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5AndMerged(chunkUploadRequestDTO.getFullFileMd5(), false);
        if (allChunks.size() != chunkUploadRequestDTO.getTotalChunkCount().longValue()) {
            return new ChunkUploadResponseDTO(chunkUploadRequestDTO.getFullFileMd5(), chunkUploadRequestDTO.getNumber(), false, false);
        }


        return null;
    }
}
