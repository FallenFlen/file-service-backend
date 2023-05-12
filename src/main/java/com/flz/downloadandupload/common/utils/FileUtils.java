package com.flz.downloadandupload.common.utils;

import com.flz.downloadandupload.common.constant.FileConstant;
import com.flz.downloadandupload.domain.valueobject.FileValueObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;

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
    @Value("${file..upload.base-path}")
    private String baseUploadPathStr;
    private static Path baseUploadPath;

    public boolean validateMd5(String currentMd5, String path) {
        if (!exists(path)) {
            return false;
        }
        byte[] content = getContent(path);
        return DigestUtils.md5DigestAsHex(content).equals(currentMd5);
    }

    public void delete(String path) {
        if (!exists(path)) {
            return;
        }

        try {
            Files.delete(Path.of(path));
        } catch (IOException e) {
            log.error("file delete failed:", e);
        }
    }

    public boolean exists(String path) {
        return Files.exists(Path.of(path));
    }

    public boolean exists(Path path) {
        return Files.exists(path);
    }

    public byte[] getContent(String path) {
        try {
            return Files.readAllBytes(Path.of(path));
        } catch (IOException e) {
            log.error("common download file failed:", e);
            return new byte[0];
        }
    }

    public void append(String path, InputStream inputStream) {
        try {
            Files.write(Path.of(path), inputStream.readAllBytes(), StandardOpenOption.APPEND);
        } catch (IOException e) {
            log.error("append file failed:", e);
            throw new RuntimeException(e);
        }
    }

    public FileValueObject uploadToDisk(String originalFileName, InputStream inputStream, OpenOption option) {
        try {
            String suffix = getSuffix(originalFileName);
            String pureFileName = getPureFileName(originalFileName);
            String mixedFileName = String.join(FileConstant.FILE_NAME_SEPARATOR,
                            pureFileName,
                            String.valueOf(System.currentTimeMillis()),
                            UUID.randomUUID().toString())
                    .concat(".")
                    .concat(suffix);
            Path filePath = Path.of(baseUploadPath.toString()
                    .concat(FileConstant.FILE_SEPARATOR)
                    .concat(mixedFileName));
            if (!Set.of(StandardOpenOption.CREATE, StandardOpenOption.CREATE_NEW).contains(option) && !exists(filePath)) {
                Files.createFile(filePath);
            }
            Files.write(filePath, inputStream.readAllBytes(), option);
            return new FileValueObject(mixedFileName, filePath.toString());
        } catch (IOException e) {
            log.error("upload file failed:", e);
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
        baseUploadPath = Path.of(baseUploadPathStr.replace("/", FileConstant.FILE_SEPARATOR));

        if (!Files.exists(baseUploadPath)) {
            Files.createDirectory(baseUploadPath);
        }
    }
}
