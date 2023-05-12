package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class FullFileCleanHandler implements FileCleanHandler {
    @Override
    public void handle(File file) {

    }
}
