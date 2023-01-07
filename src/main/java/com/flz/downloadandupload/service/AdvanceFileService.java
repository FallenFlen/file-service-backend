package com.flz.downloadandupload.service;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.enums.FileUploadRecordStatus;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdvanceFileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;

    @Transactional
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO chunkUploadRequestDTO) {
//      1.将分块内容写入到本地文件
//      - 整体文件md5检查文件是否已被上传过，如果是则实现秒传
        Optional<FileUploadRecord> fileUploadRecordOptional = fileUploadRecordDomainRepository
                .findByMd5AndStatus(chunkUploadRequestDTO.getFullFileMd5(), FileUploadRecordStatus.COMPLETED);
        if (fileUploadRecordOptional.isPresent()) {
            return new ChunkUploadResponseDTO(fileUploadRecordOptional
                    .map(FileUploadRecord::getId)
                    .get(), null);
        }
//                - 保存分块信息至db
//		○ 关联文件信息id
//                - 保存整体文件信息至db
//		○ 如果所有分块没上传完，则状态为未完成
//		○ 如果所有分块上传完成，状态更新为已完成，
//        - 文件合并
//		○ 查询所有该文件分块，并按分块号的顺序进行合并（每个分块的内容追加）
//        - 删除分块记录
//        逻辑删除

        return null;
    }
}
