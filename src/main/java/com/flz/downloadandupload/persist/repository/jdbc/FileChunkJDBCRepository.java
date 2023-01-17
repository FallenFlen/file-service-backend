package com.flz.downloadandupload.persist.repository.jdbc;

import com.flz.downloadandupload.persist.dataobject.FileChunkDO;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface FileChunkJDBCRepository extends CrudRepository<FileChunkDO, String> {
    Optional<FileChunkDO> findByIdAndDeletedIsFalse(String id);

    Optional<FileChunkDO> findFirstByMd5AndDeletedIsFalse(String md5);

    List<FileChunkDO> findAllByFullFileMd5AndDeletedIsFalse(String md5);

    @Modifying
    @Query("delete from `file_chunk` where `full_file_md5`=:md5")
    List<Integer> deleteAllByFullFileMd5(String md5);

}
