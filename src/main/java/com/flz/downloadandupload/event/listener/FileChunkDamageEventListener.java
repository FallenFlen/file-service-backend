package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.FileChunk;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.event.FileChunkDamageEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileChunkDamageEventListener {
    private final FileChunkDomainRepository fileChunkDomainRepository;
    private final FileUtils fileUtils;

    @Async
    @EventListener
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void listen(FileChunkDamageEvent event) {
        List<FileChunk> damagedChunks = event.getSource();

        // 删除损坏的chunk的表数据
        List<String> ids = damagedChunks.stream()
                .map(FileChunk::getId)
                .collect(Collectors.toList());
        fileChunkDomainRepository.deleteByIds(ids);

        // 删除损坏的chunk文件
        damagedChunks.stream()
                .map(FileChunk::getPath)
                .filter(fileUtils::exists)
                .forEach(fileUtils::delete);
    }
}
