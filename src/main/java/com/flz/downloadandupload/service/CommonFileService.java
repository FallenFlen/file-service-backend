package com.flz.downloadandupload.service;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.common.utils.ResponseUtils;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.dto.response.FileUploadResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommonFileService {
    private final FileUtils fileUtils;

    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;

    public FileUploadResponseDTO upload(MultipartFile file) throws IOException {
        FileValueObject uploadResult = fileUtils.uploadToDisk(file.getOriginalFilename(), file.getInputStream(), StandardOpenOption.TRUNCATE_EXISTING);
        FileUploadRecordCreateCommand command = FileUploadRecordCreateCommand.builder()
                .name(uploadResult.getName())
                .path(uploadResult.getPath())
                .size(file.getSize())
                .md5(DigestUtils.md5DigestAsHex(file.getInputStream()))
                .build();
        FileUploadRecord fileUploadRecord = FileUploadRecord.create(command);
        fileUploadRecordDomainRepository.saveAll(List.of(fileUploadRecord));
        return new FileUploadResponseDTO(fileUploadRecord.getPath());
    }

    public void download(String path, HttpServletResponse response) {
        FileUploadRecord fileUploadRecord = fileUploadRecordDomainRepository.findByPath(path);
        byte[] content = fileUtils.getContent(fileUploadRecord.getPath());
        ResponseUtils.download(response, fileUploadRecord.getPath(), content);
    }
}
