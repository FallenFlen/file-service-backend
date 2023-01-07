package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import org.springframework.data.repository.CrudRepository;

public interface FileChunkJDBCRepository extends CrudRepository<FileChunkDO, String> {
}
