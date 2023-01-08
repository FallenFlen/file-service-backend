package com.flz.downloadandupload.common.utils;

import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;

@Component
@Slf4j
public class FileUtils implements InitializingBean {
    @Value("${file.common.upload.base-path}")
    private String commonUploadBasePathStr;
    @Value("${file.common.upload.base-path}")
    private String advanceUploadBasePathStr;
    private Path commonUploadBasePath;
    private Path advanceUploadBasePath;

    private static final String FILE_SEPARATOR = File.separator;
    private static final String DOT = ".";
    private static final String FILE_NAME_SEPARATOR = "-";

    public byte[] getContent(String path) {
        Path filePath = Path.of(commonUploadBasePathStr.concat(FILE_SEPARATOR).concat(path));
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            log.error("common download file failed:{}", e);
            throw new RuntimeException("file download failed:" + e.getMessage());
        }
    }

    public FileValueObject uploadToDisk(String originalFileName, InputStream inputStream) {
        try {
            String suffix = getSuffix(originalFileName);
            String pureFileName = getPureFileName(originalFileName);
            String mixedFileName = String.join(FILE_NAME_SEPARATOR,
                            pureFileName,
                            String.valueOf(System.currentTimeMillis()),
                            UUID.randomUUID().toString())
                    .concat(DOT)
                    .concat(suffix);
            Path filePath = Path.of(commonUploadBasePath.toString()
                    .concat(FILE_SEPARATOR)
                    .concat(mixedFileName));
            Files.createFile(filePath);
            Files.write(filePath, inputStream.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING);
            return new FileValueObject(mixedFileName, filePath.toString());
        } catch (IOException e) {
            log.error("upload file failed:{}", e);
            throw new RuntimeException(e);
        }
    }

    private String getSuffix(String originalFileName) {
        int index = originalFileName.lastIndexOf(".");
        return index == -1 ? "" : originalFileName.substring(index + 1);
    }

    private String getPureFileName(String originalFileName) {
        int index = originalFileName.lastIndexOf(".");
        return index == -1 ? originalFileName : originalFileName.substring(0, index);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.commonUploadBasePath = Path.of(commonUploadBasePathStr.replace("/", FILE_SEPARATOR));
        this.advanceUploadBasePath = Path.of(advanceUploadBasePathStr.replace("/", FILE_SEPARATOR));

        if (!Files.exists(commonUploadBasePath)) {
            Files.createDirectory(commonUploadBasePath);
        }

        if (!Files.exists(advanceUploadBasePath)) {
            Files.createDirectory(advanceUploadBasePath);
        }
    }
}
