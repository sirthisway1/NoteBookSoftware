package com.example.markdown_demo.common.lang;


public class JwtValidationException extends RuntimeException {
    private final String statusCode;

    public JwtValidationException(String statusCode, String message) {
        super(message);
        this.statusCode = statusCode;
    }

    public String getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "JwtValidationException{" +
                "statusCode='" + statusCode + '\'' +
                ", message='" + getMessage() + '\'' +
                '}';
    }
}