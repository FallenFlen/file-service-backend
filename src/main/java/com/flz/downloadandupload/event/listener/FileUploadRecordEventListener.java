package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import com.flz.downloadandupload.event.FileUploadRecordEvent;
import com.flz.downloadandupload.service.FileUploadRecordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class FileUploadRecordEventListener {
    private final FileUploadRecordService fileUploadRecordService;

    @EventListener
    public void listen(FileUploadRecordEvent event) {
        FileValueObject fileValueObject = (FileValueObject) event.getSource();
        fileUploadRecordService.record(List.of(fileValueObject));
        log.info("file upload event occurred, name:{}, path:{}", fileValueObject.getName(), fileValueObject.getPath());
    }
}
