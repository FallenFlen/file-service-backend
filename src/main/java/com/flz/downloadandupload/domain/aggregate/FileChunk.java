package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileChunkCreateCommand;
import com.flz.downloadandupload.domain.enums.FileType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class FileChunk extends AuditAggregateRoot implements File {
    private Integer number;
    private Long standardSize;
    private Long currentSize;
    private String path;
    private String md5;
    private String fileUploadRecordId;
    private Long totalChunkCount;
    private String fullFileName;
    private String fullFileMd5;

    public static FileChunk create(FileChunkCreateCommand command) {
        return FileChunk.builder()
                .number(command.getNumber())
                .standardSize(command.getStandardSize())
                .currentSize(command.getCurrentSize())
                .path(command.getPath())
                .totalChunkCount(command.getTotalChunkCount())
                .fullFileName(command.getFullFileName())
                .fullFileMd5(command.getFullFileMd5())
                .md5(command.getMd5())
                .build();
    }


    @Override
    public String withMd5() {
        return this.md5;
    }

    @Override
    public String withPath() {
        return this.path;
    }

    @Override
    public FileType withFileType() {
        return FileType.CHUNK;
    }
}
