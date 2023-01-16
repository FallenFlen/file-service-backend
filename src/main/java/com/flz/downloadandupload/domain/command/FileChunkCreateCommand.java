package com.flz.downloadandupload.domain.command;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileChunkCreateCommand {
    private Integer number;
    private Long standardSize;
    private Long currentSize;
    private String path;
    private String md5;
    private Long totalChunkCount;
    private String fullFileName;
    private String fullFileMd5;
}
