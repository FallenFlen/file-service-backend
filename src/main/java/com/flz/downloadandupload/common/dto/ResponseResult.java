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

    public static ResponseResult withDefault(Object data) {
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
