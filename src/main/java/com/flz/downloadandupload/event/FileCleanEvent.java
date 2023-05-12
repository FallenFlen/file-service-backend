package com.flz.downloadandupload.event;

import com.flz.downloadandupload.domain.aggregate.File;
import org.springframework.context.ApplicationEvent;

public class FileCleanEvent extends ApplicationEvent {
    public FileCleanEvent(File source) {
        super(source);
    }

    @Override
    public File getSource() {
        return (File) super.getSource();
    }
}
