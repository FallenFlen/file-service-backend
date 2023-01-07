package com.flz.downloadandupload.persist.dataobject;

import com.flz.downloadandupload.persist.dataobject.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table("file_chunk")
public class FileChunkDO extends BaseDO {
    private Integer number;
    private Long standardSize;
    private Long currentSize;
    private String path;
    private String fileUploadRecordId;
    private Long totalChunkCount;
    private String fullFileName;
    private String fullFileMd5;
}
