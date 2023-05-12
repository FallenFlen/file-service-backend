package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ChunkFileCleanHandler implements FileCleanHandler {
    private final FileUtils fileUtils;
    private final FileChunkDomainRepository fileChunkDomainRepository;

    @Override
    public void handle(List<? extends File> files) {
        List<String> ids = files.stream()
                .map(File::withUniqueKey)
                .collect(Collectors.toList());
        fileChunkDomainRepository.deleteByIds(ids);
        files.stream()
                .map(File::withPath)
                .forEach(fileUtils::delete);
    }
}
