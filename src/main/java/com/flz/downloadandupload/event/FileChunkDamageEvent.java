package com.flz.downloadandupload.event;

import com.flz.downloadandupload.domain.aggregate.FileChunk;
import org.springframework.context.ApplicationEvent;

import java.util.List;

public class FileChunkDamageEvent extends ApplicationEvent {
    public FileChunkDamageEvent(List<FileChunk> damagedChunks) {
        super(damagedChunks);
    }

    @Override
    public List<FileChunk> getSource() {
        return (List<FileChunk>) super.getSource();
    }
}
