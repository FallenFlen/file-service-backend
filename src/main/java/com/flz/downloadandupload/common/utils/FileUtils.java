package com.flz.downloadandupload.common.utils;

import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Set;
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

    public boolean validateMd5(String currentMd5, String path) {
        return DigestUtils.md5DigestAsHex(getContent(path)).equals(currentMd5);
    }

    public void delete(String path) {
        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            log.error("file delete failed:", e);
            throw new RuntimeException("file delete failed:" + e.getMessage());
        }
    }

    public boolean exists(String path) {
        return Files.exists(Path.of(path));
    }

    public byte[] getContent(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            log.error("common download file failed:{}", e);
            throw new RuntimeException("file download failed:" + e.getMessage());
        }
    }

    public void append(String path, InputStream inputStream) {
        try {
            Files.write(Path.of(path), inputStream.readAllBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("append file failed:{}", e);
            throw new RuntimeException(e);
        }
    }

    public FileValueObject uploadToDisk(String originalFileName, InputStream inputStream, OpenOption option) {
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
            if (!Set.of(StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW).contains(option) && !Files.exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, inputStream.readAllBytes(), option);
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
