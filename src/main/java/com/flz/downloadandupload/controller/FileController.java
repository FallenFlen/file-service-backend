package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.dto.request.ChunkMergeRequestDTO;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.request.FileExistenceCheckRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkMergeResponseDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import com.flz.downloadandupload.dto.response.FileExistenceResponseDTO;
import com.flz.downloadandupload.dto.response.FileUploadRecordResponseDTO;
import com.flz.downloadandupload.service.FileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files")
public class FileController {
    private final FileService fileService;

    @GetMapping("/all")
    public List<FileUploadRecordResponseDTO> findAll() {
        return fileService.findAll();
    }

    @PostMapping("/chunk/check-existence-and-clean-damaged")
    public FileExistenceResponseDTO checkChunkExistenceAndClearDamaged(@RequestBody @Valid FileExistenceCheckRequestDTO requestDTO) {
        return fileService.checkChunkExistenceAndClearDamaged(requestDTO);
    }

    @PostMapping("/chunk")
    @ResponseStatus(HttpStatus.CREATED)
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO requestDTO) throws IOException {
        return fileService.uploadChunk(requestDTO);
    }

    @PostMapping("/chunk/merge")
    public ChunkMergeResponseDTO merge(@RequestBody @Valid ChunkMergeRequestDTO requestDTO) {
        return fileService.merge(requestDTO);
    }
}
