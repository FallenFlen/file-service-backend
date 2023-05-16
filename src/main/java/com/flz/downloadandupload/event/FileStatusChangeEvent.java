package com.flz.downloadandupload.event;

import com.flz.downloadandupload.domain.aggregate.File;
import org.springframework.context.ApplicationEvent;

import java.util.List;
import java.util.function.Consumer;

public class FileStatusChangeEvent extends ApplicationEvent {
    private Consumer<File> statusChangeConsumer;

    public FileStatusChangeEvent(List<? extends File> source, Consumer<File> statusChangeConsumer) {
        super(source);
        this.statusChangeConsumer = statusChangeConsumer;
    }

    @Override
    public List<? extends File> getSource() {
        return (List<? extends File>) super.getSource();
    }

    public Consumer<File> getStatusChangeConsumer() {
        return statusChangeConsumer;
    }
}
