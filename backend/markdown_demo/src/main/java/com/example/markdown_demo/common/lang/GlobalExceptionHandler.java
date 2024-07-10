package com.example.markdown_demo.common.lang;

import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(ShiroException.class)
    public Result handleShiroException(ShiroException e) {
        log.error("认证异常: ", e);
        return Result.fail(ResultType.UNAUTHORIZED.getCode(), e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(JwtValidationException.class)
    public Result handleJwtValidationException(JwtValidationException e) {
        log.error("JWT验证异常: ", e);
        return Result.fail(ResultType.UNAUTHORIZED.getCode(), e.getMessage());
    }
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Result handleBusinessException(BusinessException e) {
        log.error("业务异常: ", e);
        return Result.fail(e.getStatusCode(), e.getMessage());
    }


}