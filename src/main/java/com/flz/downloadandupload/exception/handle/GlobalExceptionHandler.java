package com.flz.downloadandupload.exception.handle;

import com.flz.downloadandupload.common.dto.ErrorResult;
import com.flz.downloadandupload.exception.BusinessException;
import com.flz.downloadandupload.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResult handleNotFoundException(NotFoundException e) {
        log.error("not found exception:", e);
        return ErrorResult.of(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResult handleBusinessException(BusinessException e) {
        log.error("business exception:", e);
        return ErrorResult.of(e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResult handleOtherException(Exception e) {
        log.error("exception:", e);
        return ErrorResult.of(e.getMessage());
    }
}
