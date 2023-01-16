package com.flz.downloadandupload.event;

import org.springframework.context.ApplicationEvent;

public class FileChunkDamageEvent extends ApplicationEvent {
    public FileChunkDamageEvent(Object source) {
        super(source);
    }
}
