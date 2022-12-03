package com.flz.downloadandupload.exception.handle;

import com.flz.downloadandupload.common.dto.ResponseResult;
import com.flz.downloadandupload.exception.BusinessException;
import com.flz.downloadandupload.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseResult handleNotFoundException(NotFoundException e) {
        return ResponseResult.withDefaultMessage(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseResult handleBusinessException(BusinessException e) {
        return ResponseResult.withDefaultMessage(e.getMessage());
    }
}
