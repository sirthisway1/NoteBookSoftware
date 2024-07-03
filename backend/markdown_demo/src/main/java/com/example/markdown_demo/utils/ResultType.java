package com.example.markdown_demo.utils;

import java.util.HashMap;
import java.util.Map;

public enum ResultType {
    SUCCESS("200", "成功"),
    UNAUTHORIZED("401", "未授权"),
    AGAIN_LOGIN("402", "需要重新登录"),
    FORBIDDEN("403", "禁止访问"),
    NOT_FOUND("404", "资源不存在"),
    INTERNAL_SERVER_ERROR("500", "服务器内部错误");

    private final String code;
    private final String message;

    private static final Map<String, ResultType> CODE_MAP = new HashMap<>();

    static {
        for (ResultType type : values()) {
            CODE_MAP.put(type.getCode(), type);
        }
    }

    ResultType(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public static ResultType fromCode(String code) {
        ResultType type = CODE_MAP.get(code);
        if (type == null) {
            throw new IllegalArgumentException("No matching constant for [" + code + "]");
        }
        return type;
    }

    @Override
    public String toString() {
        return "ResultType{" +
                "code='" + code + '\'' +
                ", message='" + message + '\'' +
                '}';
    }
}