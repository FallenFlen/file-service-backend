package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.enums.FileUploadRecordStatus;
import com.flz.downloadandupload.exception.BusinessException;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecord extends AuditAggregateRoot {
    private static final Long LARGE_FILE_JUDGE_SIZE = 5L * 1024L * 1024L;
    private String name;
    private String path;
    private Long size;
    private FileUploadRecordStatus status;
    private String md5;
    private Boolean isLargeFile;

    public static FileUploadRecord create(FileUploadRecordCreateCommand command) {
        return FileUploadRecord.builder()
                .name(command.getName())
                .path(command.getPath())
                .size(command.getSize())
                .md5(command.getMd5())
                .isLargeFile(Boolean.FALSE)
                .status(FileUploadRecordStatus.INCOMPLETE)
                .build();
    }

    public void upload(boolean completed) {
        this.status = completed ? FileUploadRecordStatus.COMPLETED : FileUploadRecordStatus.INCOMPLETE;
        this.isLargeFile = isLargeFile();
    }

    public void checkCanBeChunked() {
        if (!isLargeFile()) {
            throw new BusinessException("file can not be chunked, file size is less than or equal " + LARGE_FILE_JUDGE_SIZE);
        }
    }

    private boolean isLargeFile() {
        return size.compareTo(LARGE_FILE_JUDGE_SIZE) > 0;
    }
}
