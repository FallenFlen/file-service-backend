package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.common.utils.ResponseUtils;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.dto.response.FileUploadResponseDTO;
import com.flz.downloadandupload.event.FileUploadRecordEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
@RequiredArgsConstructor
public class CommonFileService {
    private final ApplicationEventPublisher eventPublisher;
    private final FileUtils fileUtils;

    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;

    public FileUploadResponseDTO upload(MultipartFile file) throws IOException {
        Pair<String, String> uploadResult = fileUtils.commonUploadToDisk(file.getOriginalFilename(), file.getInputStream())
                .join();
        eventPublisher.publishEvent(new FileUploadRecordEvent(uploadResult.getFirst(), uploadResult.getSecond()));
        return new FileUploadResponseDTO(uploadResult.getSecond());
    }

    public void download(String path, HttpServletResponse response) {
        FileUploadRecord fileUploadRecord = fileUploadRecordDomainRepository.findByPath(path);
        byte[] content = fileUtils.commonDownload(fileUploadRecord.getPath());
        ResponseUtils.responseFile(response, fileUploadRecord.getPath(), content);
    }
}
