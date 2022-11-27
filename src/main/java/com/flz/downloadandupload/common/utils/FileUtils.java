package com.flz.downloadandupload.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Component
@Slf4j
public class FileUtils implements InitializingBean {
    @Value("${file.upload.base-path}")
    private String uploadBasePath;
    @Value("${file.download.base-path}")
    private String downloadBasePath;
    private Path uploadBase;
    private Path downloadBase;
    private static final String FILE_SEPARATOR = File.separator;
    private static final String DOT = ".";
    private static final String FILE_NAME_SEPARATOR = "-";

    @Async
    public CompletableFuture<String> uploadToDisk(String originalFileName, InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prefix = getPrefix(originalFileName);
                String pureFileName = getPureFileName(originalFileName);
                String finalFileName = String.join(FILE_NAME_SEPARATOR, pureFileName, String.valueOf(System.currentTimeMillis()), UUID.randomUUID().toString());
                Path filePath = Path.of(uploadBase.toString()
                        .concat(FILE_SEPARATOR)
                        .concat(finalFileName)
                        .concat(DOT)
                        .concat(prefix == null ? "" : prefix));
                Files.createFile(filePath);
                Files.write(filePath, inputStream.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                return finalFileName;
            } catch (IOException e) {
                log.error("upload file failed:{}", e);
                throw new RuntimeException(e);
            }
        });
    }

    private String getPrefix(String originalFileName) {
        int index = originalFileName.lastIndexOf(".");
        return index == -1 ? "" : originalFileName.substring(index + 1);
    }

    private String getPureFileName(String originalFileName) {
        int index = originalFileName.lastIndexOf(".");
        return index == -1 ? originalFileName : originalFileName.substring(0, index);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        this.uploadBase = Path.of(uploadBasePath.replace("/", FILE_SEPARATOR));
        this.downloadBase = Path.of(downloadBasePath.replace("/", FILE_SEPARATOR));

        if (!Files.exists(uploadBase)) {
            Files.createDirectory(uploadBase);
        }

        if (Files.exists(downloadBase)) {
            Files.createDirectory(downloadBase);
        }
    }
}
