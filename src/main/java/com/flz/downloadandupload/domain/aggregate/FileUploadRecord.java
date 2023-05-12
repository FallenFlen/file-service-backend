package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.enums.FileSizeType;
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
    private FileSizeType type;
    private String md5;

    public static FileUploadRecord create(FileUploadRecordCreateCommand command) {
        return FileUploadRecord.builder()
                .name(command.getName())
                .path(command.getPath())
                .size(command.getSize())
                .md5(command.getMd5())
                .type(FileSizeType.calculate(command.getSize(), LARGE_FILE_SIZE_CRITICAL_VALUE))
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
        return FileType.FULL_FILE;
    }
}
