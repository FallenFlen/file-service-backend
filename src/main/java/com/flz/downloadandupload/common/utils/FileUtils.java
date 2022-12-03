package com.flz.downloadandupload.common.utils;

import com.flz.downloadandupload.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
    @Value("${file.common.upload.base-path}")
    private String commonUploadBasePathStr;
    @Value("${file.common.download.base-path}")
    private String commonDownloadBasePathStr;
    @Value("${file.common.upload.base-path}")
    private String advanceUploadBasePathStr;
    @Value("${file.common.download.base-path}")
    private String advanceDownloadBasePathStr;
    private Path commonUploadBasePath;
    private Path commonDownloadBase;
    private Path advanceUploadBasePath;
    private Path advanceDownloadBasePath;

    private static final String FILE_SEPARATOR = File.separator;
    private static final String DOT = ".";
    private static final String FILE_NAME_SEPARATOR = "-";

    public byte[] commonDownload(String path) {
        Path filePath = Path.of(commonUploadBasePathStr.concat(FILE_SEPARATOR).concat(path));
        try {
            return Files.readAllBytes(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BusinessException("file download failed:" + e.getMessage());
        }
    }

    @Async
    public CompletableFuture<Pair<String, String>> commonUploadToDisk(String originalFileName, InputStream inputStream) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String prefix = getPrefix(originalFileName);
                String pureFileName = getPureFileName(originalFileName);
                String finalFilePathStr = String.join(FILE_NAME_SEPARATOR,
                                pureFileName,
                                String.valueOf(System.currentTimeMillis()),
                                UUID.randomUUID().toString())
                        .concat(DOT)
                        .concat(prefix);
                Path filePath = Path.of(commonUploadBasePath.toString()
                        .concat(FILE_SEPARATOR)
                        .concat(finalFilePathStr));
                Files.createFile(filePath);
                Files.write(filePath, inputStream.readAllBytes(), StandardOpenOption.TRUNCATE_EXISTING);
                // 原始未加工的文件名需要保存至上传记录的db中
                return Pair.of(pureFileName, finalFilePathStr);
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
        this.commonUploadBasePath = Path.of(commonUploadBasePathStr.replace("/", FILE_SEPARATOR));
        this.commonDownloadBase = Path.of(commonDownloadBasePathStr.replace("/", FILE_SEPARATOR));
        this.advanceUploadBasePath = Path.of(advanceUploadBasePathStr.replace("/", FILE_SEPARATOR));
        this.advanceDownloadBasePath = Path.of(advanceDownloadBasePathStr.replace("/", FILE_SEPARATOR));

        if (!Files.exists(commonUploadBasePath)) {
            Files.createDirectory(commonUploadBasePath);
        }

        if (!Files.exists(commonDownloadBase)) {
            Files.createDirectory(commonDownloadBase);
        }

        if (!Files.exists(advanceUploadBasePath)) {
            Files.createDirectory(advanceUploadBasePath);
        }

        if (!Files.exists(advanceDownloadBasePath)) {
            Files.createDirectory(advanceDownloadBasePath);
        }
    }
}
