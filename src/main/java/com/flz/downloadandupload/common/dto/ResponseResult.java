package com.flz.downloadandupload.common.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResponseResult {
    private Object data;
    private String message;

    public static ResponseResult withDefaultMessage(String message) {
        return ResponseResult.builder()
                .message(message)
                .build();
    }

    public static ResponseResult withDefaultData(Object data) {
        return ResponseResult.builder()
                .data(data)
                .message("success")
                .build();
    }

    public ResponseResult withMessage(String message) {
        this.message = message;
        return this;
    }

    public ResponseResult withData(Object data) {
        this.data = data;
        return this;
    }
}
