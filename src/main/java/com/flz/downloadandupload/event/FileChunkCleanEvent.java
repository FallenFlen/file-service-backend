package com.flz.downloadandupload.event;

import org.springframework.context.ApplicationEvent;

public class FileChunkCleanEvent extends ApplicationEvent {
    public FileChunkCleanEvent(String source) {
        super(source);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }
}
