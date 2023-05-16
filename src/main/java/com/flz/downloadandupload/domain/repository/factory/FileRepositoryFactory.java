package com.flz.downloadandupload.domain.repository.factory;

import com.flz.downloadandupload.common.utils.ApplicationContextUtils;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.domain.repository.FileChunkDomainRepository;
import com.flz.downloadandupload.domain.repository.FileRepository;
import com.flz.downloadandupload.domain.repository.FileUploadRecordDomainRepository;
import com.flz.downloadandupload.exception.BusinessException;

public class FileRepositoryFactory {
    public static FileRepository withRepository(FileType fileType) {
        switch (fileType) {
            case FULL_FILE:
                return ApplicationContextUtils.getBean(FileUploadRecordDomainRepository.class);
            case CHUNK:
                return ApplicationContextUtils.getBean(FileChunkDomainRepository.class);
            default:
                throw new BusinessException("no repository found with type:" + fileType);
        }
    }
}
