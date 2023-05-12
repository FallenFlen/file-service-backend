package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FullFileCleanHandler extends AbstractFileCleanHandler {
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;

    @Override
    protected void deleteDbRecords(List<? extends File> files) {
        List<String> ids = files.stream()
                .map(File::withUniqueKey)
                .collect(Collectors.toList());
        fileUploadRecordDomainRepository.deleteByIds(ids);
    }
}
