package com.flz.downloadandupload.service.handler;

import com.flz.downloadandupload.common.utils.ApplicationContextUtils;
import com.flz.downloadandupload.domain.enums.FileType;
import com.flz.downloadandupload.exception.BusinessException;

public class FileCleanHandlerFactory {
    public static FileCleanHandler withHandler(FileType fileType) {
        switch (fileType) {
            case FULL_FILE:
                return ApplicationContextUtils.getBean(FullFileCleanHandler.class);
            case CHUNK:
                return ApplicationContextUtils.getBean(ChunkFileCleanHandler.class);
            default:
                throw new BusinessException("File clean handler not found with type:" + fileType);
        }
    }
}
