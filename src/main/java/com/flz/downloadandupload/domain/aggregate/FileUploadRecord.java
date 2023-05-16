package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.enums.FileStatus;
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
public class FileUploadRecord extends AuditAggregateRoot implements File {
    private static final long LARGE_FILE_SIZE_CRITICAL_VALUE = 5 * 1024 * 1024;

    private String name;
    private String path;
    private Long size;
    private String md5;
    private FileStatus status;

    public static FileUploadRecord create(FileUploadRecordCreateCommand command) {
        return FileUploadRecord.builder()
                .name(command.getName())
                .path(command.getPath())
                .size(command.getSize())
                .md5(command.getMd5())
                .status(FileStatus.NORMAL)
                .build();
    }

    public void damage() {
        this.status = FileStatus.DAMAGED;
    }

    @Override
    public String withUniqueKey() {
        return getId();
    }

    @Override
    public String withPath() {
        return this.path;
    }

    @Override
    public FileType withFileType() {
        return FileType.FULL_FILE;
    }
}
