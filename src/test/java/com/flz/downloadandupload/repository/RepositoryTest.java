package com.flz.downloadandupload.repository;

import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import com.flz.downloadandupload.persist.repository.jdbc.FileUploadRecordJDBCRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@Rollback
public class RepositoryTest {
    @Autowired
    private FileUploadRecordJDBCRepository jdbcRepository;

    @Test
    void should_save_do_successfully() {
        FileUploadRecordDO fileUploadRecordDO = new FileUploadRecordDO();
        fileUploadRecordDO.setName("test中文");
        fileUploadRecordDO.setPath("test");
        FileUploadRecordDO save = jdbcRepository.save(fileUploadRecordDO);
        System.out.println(save.getId());
        System.out.println(save.getName());
    }
}
