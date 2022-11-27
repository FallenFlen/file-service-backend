package com.flz.downloadandupload.domain.command;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileUploadRecordCreateCommand {
    private String name;
    private String path;
}
