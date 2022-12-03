package com.flz.downloadandupload.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;

@Slf4j
public class ResponseUtils {

    public static void responseFile(HttpServletResponse response, String fileName, byte[] content) {
        try {
            response.setHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(fileName, "UTF-8"));
            response.getOutputStream().write(content, 0, content.length);
        } catch (IOException e) {
            log.error("response file error:{}", e);
            throw new RuntimeException(e);
        }
    }
}
