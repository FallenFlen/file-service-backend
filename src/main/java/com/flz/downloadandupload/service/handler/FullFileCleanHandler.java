package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.domain.aggregate.File;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class FullFileCleanHandler implements FileCleanHandler {
    @Override
    public void handle(List<File> files) {

    }
}
