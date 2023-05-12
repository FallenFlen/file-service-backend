package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.event.FileCleanEvent;
import com.flz.downloadandupload.service.handler.FileCleanHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class FileCleanListener {

    @Async
    @EventListener
    public void listen(FileCleanEvent event) {
        List<File> files = event.getSource();
        Map<FileType, List<File>> fileGroup = files.stream()
                .collect(Collectors.groupingBy(File::withFileType));
        fileGroup.forEach((k, v) -> {
            FileCleanHandlerFactory.withHandler(k)
                    .handle(v);
        });
    }
}
