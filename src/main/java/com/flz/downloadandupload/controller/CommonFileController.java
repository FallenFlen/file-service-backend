package com.flz.downloadandupload.controller;

import com.flz.downloadandupload.common.dto.ResponseResult;
import com.flz.downloadandupload.service.CommonFileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/files/common")
public class CommonFileController {
    private final CommonFileService commonFileService;

    @PostMapping("/upload")
    public ResponseResult upload(@RequestParam("file") MultipartFile file) throws IOException {
        String filePath = commonFileService.upload(file);
        return ResponseResult.withDefault(filePath)
                .withMessage("upload file successfully");
    }
}
