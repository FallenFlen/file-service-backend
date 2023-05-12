package com.flz.downloadandupload.persist.dataobject;

import com.flz.downloadandupload.domain.enums.FileSizeType;
import com.flz.downloadandupload.persist.dataobject.base.BaseDO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table("file_upload_record")
public class FileUploadRecordDO extends BaseDO {
    private String name;
    private String path;
    private Long size;
    private FileSizeType type;
    private String md5;
}
