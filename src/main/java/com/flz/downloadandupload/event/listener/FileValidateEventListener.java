package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.event.FileValidateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class FileValidateEventListener {
    private final FileUploadRecordDomainRepository fileUploadRecordDomainRepository;
    private final FileUtils fileUtils;

    @Async
    @EventListener
    public void listen(FileValidateEvent event) {
        String fullFileMd5 = event.getSource();
        Optional.ofNullable(fileUploadRecordDomainRepository.findByMd5(fullFileMd5))
                .ifPresent((record) -> {
                    String path = record.getPath();
                    boolean exists = fileUtils.exists(path);
                    if (exists) {
                        boolean md5Correct = fileUtils.validateMd5(record.getMd5(), path);
                        if (!md5Correct) {
                            fileUtils.delete(path);
                            fileUploadRecordDomainRepository.deleteById(record.getId());
                        }
                    } else {
                        fileUploadRecordDomainRepository.deleteById(record.getId());
                    }
                });
    }
}
