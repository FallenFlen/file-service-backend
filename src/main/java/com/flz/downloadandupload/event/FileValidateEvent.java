package com.flz.downloadandupload.event;

import org.springframework.context.ApplicationEvent;

public class FileValidateEvent extends ApplicationEvent {
    public FileValidateEvent(String source) {
        super(source);
    }

    @Override
    public String getSource() {
        return (String) super.getSource();
    }
}
