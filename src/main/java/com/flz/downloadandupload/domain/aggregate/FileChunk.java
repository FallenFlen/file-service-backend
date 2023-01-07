package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileChunk extends AuditAggregateRoot {
    private Integer number;
    private Long standardSize;
    private Long currentSize;
    private String path;
    private String content;
    private Long totalChunkCount;
    private String fileName;
    private String fullFileMd5;

    public static FileChunk create(FileChunkCreateCommand command) {
        return FileChunk.builder()
                .number(command.getNumber())
                .standardSize(command.getStandardSize())
                .currentSize(command.getCurrentSize())
                .path(command.getPath())
                .build();
    }
}
