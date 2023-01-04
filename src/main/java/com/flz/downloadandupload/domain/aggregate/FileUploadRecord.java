package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.domain.enums.FileUploadRecordStatus;
import com.flz.downloadandupload.exception.BusinessException;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecord extends AuditAggregateRoot {
    private static final long LARGE_FILE_SIZE_CRITICAL_VALUE = 5 * 1024 * 1024;

    private String name;
    private String path;
    private Long size;
    private FileUploadRecordStatus status;
    private FileType type;
    private String md5;

    public static FileUploadRecord create(FileUploadRecordCreateCommand command) {
        return FileUploadRecord.builder()
                .name(command.getName())
                .path(command.getPath())
                .size(command.getSize())
                .md5(command.getMd5())
                .type(FileType.calculate(command.getSize(), LARGE_FILE_SIZE_CRITICAL_VALUE))
                .status(FileUploadRecordStatus.INCOMPLETE)
                .build();
    }

    public void upload(boolean completed) {
        this.status = completed ? FileUploadRecordStatus.COMPLETED : FileUploadRecordStatus.INCOMPLETE;
    }

    public void checkCanBeChunked() {
        if (this.type != FileType.LARGE_SIZE_FILE) {
            throw new BusinessException("file can not be chunked, file size is less than or equal " + LARGE_FILE_SIZE_CRITICAL_VALUE);
        }
    }
}
