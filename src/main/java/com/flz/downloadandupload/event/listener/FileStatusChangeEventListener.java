package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.domain.aggregate.File;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.domain.repository.factory.FileRepositoryFactory;
import com.flz.downloadandupload.event.FileStatusChangeEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FileStatusChangeEventListener {

    @Async
    @EventListener
    public void listen(FileStatusChangeEvent event) {
        List<? extends File> files = event.getSource();
        files.forEach(event.getStatusChangeConsumer());
        FileType fileType = files.get(0).withFileType();
        FileRepositoryFactory.withRepository(fileType).saveAll(files);
    }
}
