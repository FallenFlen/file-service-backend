package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.common.dto.ResponseResult;
import com.flz.downloadandupload.dto.response.FileUploadResponseDTO;
import com.flz.downloadandupload.service.CommonFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files/common")
public class CommonFileController {
    private final CommonFileService commonFileService;

    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        FileUploadResponseDTO responseDTO = commonFileService.upload(file);
        return ResponseResult.withDefaultData(responseDTO)
                .withMessage("upload file successfully");
    }

    @GetMapping("/download")
    public void download(@RequestParam("path") String path, HttpServletResponse response) {
        commonFileService.download(path, response);
    }
}
