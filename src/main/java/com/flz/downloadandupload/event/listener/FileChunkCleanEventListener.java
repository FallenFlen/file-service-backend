package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.event.FileChunkCleanEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileChunkCleanEventListener {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUtils fileUtils;

    @Async
    @EventListener
    public void listen(FileChunkCleanEvent event) {
        String fullFileMd5 = event.getSource();
        List<FileChunk> allChunks = fileChunkDomainRepository.findAllByFullFileMd5(fullFileMd5);

        fileChunkDomainRepository.deleteByFullFileMd5(fullFileMd5);
    }
}
