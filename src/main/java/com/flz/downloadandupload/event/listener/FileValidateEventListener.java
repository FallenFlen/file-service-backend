package com.flz.downloadandupload.event.listener;

import com.flz.downloadandupload.event.FileValidateEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class FileValidateEventListener implements Listener<FileValidateEvent> {

    @EventListener
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void listen(FileValidateEvent event) {

    }
}
