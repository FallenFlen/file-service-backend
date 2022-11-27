package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.base.AuditAggregateRoot;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecord extends AuditAggregateRoot {
    private String name;
    private String path;

    public static FileUploadRecord create(String name, String path) {
        return FileUploadRecord.builder()
                .name(name)
                .path(path)
                .build();
    }
}
