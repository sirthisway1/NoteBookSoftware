package com.example.markdown_demo.common.lang;

public class BusinessException extends RuntimeException {
    private final String statusCode;

    public BusinessException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public BusinessException(ResultType resultType) {
        this(resultType.getCode(), resultType.getMessage());
    }

    @Override
    public String toString() {
        return "BusinessException{" +
                "statusCode='" + statusCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}