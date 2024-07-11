package com.example.markdown_demo.common.lang;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result<T> {
    private String code;
    private String message;
    private T data;

    public static <T> Result<T> success(T data) {
        return new Result<>(ResultType.SUCCESS.getCode(), ResultType.SUCCESS.getMessage(), data);
    }
    public static <T> Result<T> success(String message) {
        return new Result<>(ResultType.SUCCESS.getCode(), message,null);
    }

    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultType.SUCCESS.getCode(), message, data);
    }

    public static <T> Result<T> fail(String statusCode, String message) {
        return new Result<>(statusCode, message, null);
    }

    public static <T> Result<T> fail(ResultType resultType) {
        return new Result<>(resultType.getCode(), resultType.getMessage(), null);
    }

    public static <T> Result<T> of(String statusCode, String message, T data) {
        return new Result<>(statusCode, message, data);
    }
}