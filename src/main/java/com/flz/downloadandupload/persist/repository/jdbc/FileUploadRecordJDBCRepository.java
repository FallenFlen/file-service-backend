package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileUploadRecordJDBCRepository extends CrudRepository<FileUploadRecordDO, String> {
    Optional<FileUploadRecordDO> findFirstByPathAndDeletedIsFalse(String path);

    Optional<FileUploadRecordDO> findFirstByMd5AndDeletedIsFalse(String md5);

    List<FileUploadRecordDO> findAll();
}
