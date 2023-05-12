package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;

import java.util.List;

public interface FileCleanHandler {
    void handle(List<? extends File> files);
}
