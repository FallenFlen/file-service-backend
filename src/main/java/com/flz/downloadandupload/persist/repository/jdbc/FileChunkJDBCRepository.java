package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.domain.enums.FileStatus;
import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface FileChunkJDBCRepository extends CrudRepository<FileChunkDO, String> {
    Optional<FileChunkDO> findByIdAndDeletedIsFalse(String id);

    Optional<FileChunkDO> findFirstByMd5AndDeletedIsFalse(String md5);

    List<FileChunkDO> findAllByFullFileMd5AndDeletedIsFalse(String md5);

    @Modifying
    @Query("delete from `file_chunk` where `full_file_md5`=:md5")
    List<Integer> deleteAllByFullFileMd5(String md5);

    @Query("select * from `file_chunk` where (`status`!=:status or `status` is null) and `deleted`='0' limit :limit")
    List<FileChunkDO> findAllByStatusNotEqualAndDeletedIsFalseAndLimit(@Param("status") FileStatus status, @Param("limit") int limit);
}
