package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.event.FileCleanEvent;
import com.flz.downloadandupload.service.handler.FileCleanHandlerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FileCleanListener {

    @Async
    @EventListener
    public void listen(FileCleanEvent event) {
        File file = event.getSource();
        FileCleanHandlerFactory.withHandler(file.withFileType())
                .handle(file);
    }
}
