package com.flz.downloadandupload.event;

import org.springframework.context.ApplicationEvent;

public class FileCleanEvent extends ApplicationEvent {
    public FileCleanEvent(Object source) {
        super(source);
    }

}
