package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;

public interface FileCleanHandler {
    void handle(File file);
}
