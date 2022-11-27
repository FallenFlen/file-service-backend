package com.flz.downloadandupload.event.base;

import org.springframework.context.ApplicationEvent;

public class BaseApplicationEvent extends ApplicationEvent {
    public BaseApplicationEvent(Object source) {
        super(source);
    }
}
