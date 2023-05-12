package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.common.utils.FileUtils;
import com.flz.downloadandupload.domain.aggregate.File;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;

import java.util.List;

@Slf4j
public abstract class AbstractFileCleanHandler implements FileCleanHandler {
    @Autowired
    @Lazy
    protected FileUtils fileUtils;

    @Override
    public void handle(List<? extends File> files) {
        cleanDiskFile(files);
        deleteDbRecords(files);
    }

    private void cleanDiskFile(List<? extends File> files) {
        try {
            files.stream()
                    .map(File::withPath)
                    .forEach(fileUtils::delete);
        } catch (Exception e) {
            log.error("[AbstractFileCleanHandler] clean disk file failed:", e);
        }
    }

    protected abstract void deleteDbRecords(List<? extends File> files);
}
