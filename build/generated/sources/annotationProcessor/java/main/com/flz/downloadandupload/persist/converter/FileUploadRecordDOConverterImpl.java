package com.flz.downloadandupload.persist.converter;

import com.flz.downloadandupload.domain.aggregate.FileUploadRecord;
import com.flz.downloadandupload.domain.aggregate.FileUploadRecord.FileUploadRecordBuilder;
import com.flz.downloadandupload.persist.dataobject.FileUploadRecordDO;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-05-16T22:11:42+0800",
    comments = "version: 1.4.2.Final, compiler: IncrementalProcessingEnvironment from gradle-language-java-7.5.1.jar, environment: Java 11.0.15 (Oracle Corporation)"
)
public class FileUploadRecordDOConverterImpl implements FileUploadRecordDOConverter {

    @Override
    public FileUploadRecordDO toDO(FileUploadRecord fileUploadRecord) {
        if ( fileUploadRecord == null ) {
            return null;
        }

        FileUploadRecordDO fileUploadRecordDO = new FileUploadRecordDO();

        fileUploadRecordDO.setId( fileUploadRecord.getId() );
        fileUploadRecordDO.setCreateTime( fileUploadRecord.getCreateTime() );
        fileUploadRecordDO.setUpdateTime( fileUploadRecord.getUpdateTime() );
        fileUploadRecordDO.setDeleted( fileUploadRecord.getDeleted() );
        fileUploadRecordDO.setName( fileUploadRecord.getName() );
        fileUploadRecordDO.setPath( fileUploadRecord.getPath() );
        fileUploadRecordDO.setSize( fileUploadRecord.getSize() );
        fileUploadRecordDO.setMd5( fileUploadRecord.getMd5() );
        fileUploadRecordDO.setStatus( fileUploadRecord.getStatus() );

        return fileUploadRecordDO;
    }

    @Override
    public FileUploadRecord toDomain(FileUploadRecordDO fileUploadRecordDO) {
        if ( fileUploadRecordDO == null ) {
            return null;
        }

        FileUploadRecordBuilder<?, ?> fileUploadRecord = FileUploadRecord.builder();

        fileUploadRecord.id( fileUploadRecordDO.getId() );
        fileUploadRecord.createTime( fileUploadRecordDO.getCreateTime() );
        fileUploadRecord.updateTime( fileUploadRecordDO.getUpdateTime() );
        fileUploadRecord.deleted( fileUploadRecordDO.getDeleted() );
        fileUploadRecord.name( fileUploadRecordDO.getName() );
        fileUploadRecord.path( fileUploadRecordDO.getPath() );
        fileUploadRecord.size( fileUploadRecordDO.getSize() );
        fileUploadRecord.md5( fileUploadRecordDO.getMd5() );
        fileUploadRecord.status( fileUploadRecordDO.getStatus() );

        return fileUploadRecord.build();
    }
}
