package com.flz.downloadandupload.event.listener;

import org.springframework.context.ApplicationEvent;

public interface Listener<T extends ApplicationEvent> {
    void listen(T event);
}
