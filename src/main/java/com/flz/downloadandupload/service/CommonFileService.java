package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.event.FileUploadRecordEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CommonFileService {
    private final ApplicationEventPublisher eventPublisher;
    private final FileUtils fileUtils;

    public String upload(MultipartFile file) throws IOException {
        Pair<String, String> uploadResult = fileUtils.uploadToDisk(file.getOriginalFilename(), file.getInputStream())
                .join();
        eventPublisher.publishEvent(new FileUploadRecordEvent(uploadResult.getFirst(), uploadResult.getSecond()));
        return uploadResult.getSecond();
    }
}
