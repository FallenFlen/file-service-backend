package com.flz.downloadandupload.service;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class FileUploadRecordService {
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;

    public void record(List<FileValueObject> files) {
        List<FileUploadRecord> fileUploadRecords = files.stream()
                .map((file) -> FileUploadRecordCreateCommand.builder()
                        .name(file.getName())
                        .path(file.getPath())
                        .build())
                .map(FileUploadRecord::create)
                .collect(Collectors.toList());
        fileUploadRecordDomainRepository.saveAll(fileUploadRecords);
    }
}
