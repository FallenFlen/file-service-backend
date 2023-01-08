package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.dto.request.ChunkMergeRequestDTO;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkMergeResponseDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import com.flz.downloadandupload.service.AdvanceFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files/advance")
public class AdvanceFileController {
    private final AdvanceFileService advanceFileService;

    @PostMapping("/chunk")
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO requestDTO) throws IOException {
        return advanceFileService.uploadChunk(requestDTO);
    }

    @PostMapping("/chunk/merge")
    public ChunkMergeResponseDTO merge(@RequestBody @Valid ChunkMergeRequestDTO requestDTO) throws IOException {
        return advanceFileService.merge(requestDTO);
    }
}
