package com.flz.downloadandupload.service;

import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.dto.request.ChunkRequestDTO;
import com.flz.downloadandupload.dto.response.ResourceCreationResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvanceFileService {
    private final FileChunkDomainRepository fileChunkDomainRepository;

    @Transactional
    public ResourceCreationResponseDTO uploadChunk(ChunkRequestDTO chunkRequestDTO) {
        return null;
    }
}
