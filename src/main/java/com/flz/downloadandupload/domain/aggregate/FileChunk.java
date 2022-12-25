package com.flz.downloadandupload.domain.aggregate;

import com.flz.downloadandupload.domain.aggregate.base.AuditAggregateRoot;
import lombok.*;

@Getter
@Setter(AccessLevel.PROTECTED)
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileChunk extends AuditAggregateRoot {
    private Integer number;
    private Long standardSize;
    private Long currentSize;
    private String path;
    private String content;
    private Long totalChunkCount;
    private String fileName;
    private String fullFileMd5;
}
