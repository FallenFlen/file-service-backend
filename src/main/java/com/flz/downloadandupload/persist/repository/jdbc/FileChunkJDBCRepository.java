package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface FileChunkJDBCRepository extends CrudRepository<FileChunkDO, String> {
    Optional<FileChunkDO> findByIdAndDeletedIsFalse(String id);
}
