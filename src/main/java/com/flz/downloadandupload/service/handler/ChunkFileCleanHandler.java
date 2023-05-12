package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChunkFileCleanHandler extends AbstractFileCleanHandler {
    private final FileChunkDomainRepository fileChunkDomainRepository;

    @Override
    protected void deleteDbRecords(List<? extends File> files) {
        List<String> ids = files.stream()
                .map(File::withUniqueKey)
                .collect(Collectors.toList());
        fileChunkDomainRepository.deleteByIds(ids);
    }
}
