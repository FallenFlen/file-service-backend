package com.flz.downloadandupload.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
public class ResponseUtils {

    public static void download(HttpServletResponse response, String fileName, byte[] content) {
        try {
            assembleDownloadHeader(response, fileName);
            response.getOutputStream().write(content, 0, content.length);
        } catch (Exception e) {
            log.error("response file error:{}", e);
            throw new RuntimeException(e);
        }
    }

    private static void assembleDownloadHeader(HttpServletResponse response, String fileName) throws UnsupportedEncodingException {
        response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
    }

    private static void assembleDownloadHeaderWithChunk(HttpServletResponse response, String fileName, long size) throws UnsupportedEncodingException {
        assembleDownloadHeader(response, fileName);
        //支持断点续传，获取部分字节内容：
        response.setHeader("Accept-Ranges", "bytes");
        //http状态码要为206：表示获取部分内容
        response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);

        // 获取请求头Range bytes=[start]-[end]
        String range = RequestUtils.getFirstHeader("Range");
        long startByte = 0L;
        long endByte = size;
        int rangeStartIndex = range.indexOf("=");
        // [start]-[end]
        range = range.substring(rangeStartIndex + 1).trim();
        int rangeSplitIndex = range.indexOf("-");
        if (range.startsWith("-")) {
            startByte = Long.parseLong(range.substring(rangeSplitIndex + 1));
        } else if (range.endsWith("-")) {
            endByte = Long.parseLong(range.substring(0, rangeSplitIndex));
        } else {
            startByte = Long.parseLong(range.substring(0, rangeSplitIndex));
            endByte = Long.parseLong(range.substring(rangeSplitIndex + 1));
        }

        // Content-Range，格式为：[要下载的开始位置]-[结束位置]/[文件总大小]
        response.setHeader("Content-Range", "bytes " + startByte + "-" + endByte + "/" + size);
    }

    public static void downloadWithChunk(HttpServletResponse response, String fileName, String path, long size) {
        try {
            assembleDownloadHeaderWithChunk(response, fileName, size);
            FileInputStream fileInputStream = new FileInputStream(path);
            byte[] buffer = new byte[5 * 1024 * 1024];
            ServletOutputStream responseOutputStream = response.getOutputStream();
            BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(responseOutputStream);
            int len;
            while ((len = fileInputStream.read(buffer)) != -1) {
                bufferedOutputStream.write(buffer, 0, len);
            }
            bufferedOutputStream.flush();
            response.flushBuffer();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
