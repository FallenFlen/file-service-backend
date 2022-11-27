package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import org.springframework.data.repository.CrudRepository;

public interface FileUploadRecordJDBCRepository extends CrudRepository<FileUploadRecordDO, String> {
}
