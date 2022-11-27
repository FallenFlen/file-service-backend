package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.base.AuditAggregateRoot;
import com.flz.downloadandupload.domain.command.FileUploadRecordCreateCommand;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecord extends AuditAggregateRoot {
    private String name;
    private String path;

    public static FileUploadRecord create(FileUploadRecordCreateCommand command) {
        return FileUploadRecord.builder()
                .name(command.getName())
                .path(command.getPath())
                .build();
    }
}
