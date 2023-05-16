package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.enums.FileStatus;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.event.FileCleanEvent;
import com.flz.downloadandupload.service.handler.FileCleanHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileCleanListener {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private static final int MAX_FETCH_COUNT = 5000;

    @Async
    @EventListener
    public void listen(FileCleanEvent event) {
        List<File> files = fetchStatusChangedFiles();
        Map<FileType, List<File>> fileGroup = files.stream()
                .collect(Collectors.groupingBy(File::withFileType));
        fileGroup.forEach((k, v) -> {
            FileCleanHandlerFactory.withHandler(k)
                    .handle(v);
        });
    }

    private List<File> fetchStatusChangedFiles() {
        List<FileChunk> chunks = fileChunkDomainRepository.findAllByStatusNotEqualAndLimit(FileStatus.NORMAL, MAX_FETCH_COUNT);
        List<FileUploadRecord> fileUploadRecords = fileUploadRecordDomainRepository.findAllByStatusNotEqualAndLimit(FileStatus.NORMAL, MAX_FETCH_COUNT);
        List<File> result = new ArrayList<>();
        result.addAll(chunks);
        result.addAll(fileUploadRecords);
        return result;
    }
}
