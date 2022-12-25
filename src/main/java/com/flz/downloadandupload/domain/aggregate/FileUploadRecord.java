package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import com.flz.downloadandupload.domain.enums.FileUploadRecordStatus;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecord extends AuditAggregateRoot {
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
                .build();
    }
}
