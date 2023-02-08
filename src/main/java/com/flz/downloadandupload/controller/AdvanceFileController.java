package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.dto.request.ChunkMergeRequestDTO;
import com.flz.downloadandupload.dto.request.ChunkUploadRequestDTO;
import com.flz.downloadandupload.dto.request.FileExistenceCheckRequestDTO;
import com.flz.downloadandupload.dto.response.ChunkMergeResponseDTO;
import com.flz.downloadandupload.dto.response.ChunkUploadResponseDTO;
import com.flz.downloadandupload.dto.response.FileExistenceResponseDTO;
import com.flz.downloadandupload.dto.response.FileUploadRecordResponseDTO;
import com.flz.downloadandupload.service.AdvanceFileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/files/advance")
public class AdvanceFileController {
    private final AdvanceFileService advanceFileService;

    @GetMapping("/all")
    public List<FileUploadRecordResponseDTO> findAll() {
        return advanceFileService.findAll();
    }

    @GetMapping("/download")
    public void download(@RequestParam String path, HttpServletResponse response) throws IOException {
        advanceFileService.download(path, response);
    }

    @PostMapping("/chunk/validate-and-clean-damaged")
    public FileExistenceResponseDTO checkFileExistenceAndClearDamaged(@RequestBody @Valid FileExistenceCheckRequestDTO requestDTO) {
        return advanceFileService.checkFileExistenceAndClearDamaged(requestDTO);
    }

    @PostMapping("/chunk")
    @ResponseStatus(HttpStatus.CREATED)
    public ChunkUploadResponseDTO uploadChunk(ChunkUploadRequestDTO requestDTO) throws IOException {
        return advanceFileService.uploadChunk(requestDTO);
    }

    @PostMapping("/chunk/merge")
    public ChunkMergeResponseDTO merge(@RequestBody @Valid ChunkMergeRequestDTO requestDTO) throws IOException {
        return advanceFileService.merge(requestDTO);
    }
}
