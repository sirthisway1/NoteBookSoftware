package com.example.markdown_demo.common.lang;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.ShiroException;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authz.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;

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



    /**
     * @Validated 校验错误异常处理
     */
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(RuntimeException.class)
//    public Result handler(RuntimeException e) throws IOException {
//        log.error("运行时异常:-------------->",e);
//        return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(), e.getMessage());
//    }

//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    @ExceptionHandler(ResourceNotFoundException.class)
//    public Result handleResourceNotFoundException(ResourceNotFoundException e) {
//        log.error("资源不存在: ", e);
//        return Result.fail(ResultType.RESOURCE_NOT_FOUND.getCode(), ResultType.RESOURCE_NOT_FOUND.getMessage());
//    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler(Exception.class)
//    public Result handleException(Exception e) {
//        log.error("服务器异常: ", e);
//        return Result.fail(ResultType.INTERNAL_SERVER_ERROR.getCode(), ResultType.INTERNAL_SERVER_ERROR.getMessage());
//    }
}