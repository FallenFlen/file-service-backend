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
    private String content;
    private Long totalChunkCount;
    private String fileName;
    private String fullFileMd5;
}
