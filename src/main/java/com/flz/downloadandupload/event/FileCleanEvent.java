package com.flz.downloadandupload.event;

import com.flz.downloadandupload.domain.aggregate.File;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class FileCleanEvent extends ApplicationEvent {
    public FileCleanEvent(List<File> source) {
        super(source);
    }

    @Override
    public List<File> getSource() {
        return (List<File>) super.getSource();
    }
}
