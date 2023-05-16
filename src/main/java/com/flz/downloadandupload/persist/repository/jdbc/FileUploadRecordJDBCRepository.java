package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.domain.enums.FileStatus;
import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileUploadRecordJDBCRepository extends CrudRepository<FileUploadRecordDO, String> {
    Optional<FileUploadRecordDO> findFirstByPathAndDeletedIsFalse(String path);

    Optional<FileUploadRecordDO> findFirstByMd5AndDeletedIsFalse(String md5);

    List<FileUploadRecordDO> findAll();

    @Modifying
    @Query("delete from file_upload_record where id in(:ids)")
    void deleteAllByIds(@Param("ids") List<String> ids);


    @Query("select * from `file_upload_record` where (`status`!=:status or `status` is null) and `deleted`='0' limit :limit")
    List<FileUploadRecordDO> findAllByStatusNotEqualAndLimit(FileStatus status, int limit);
}
