package com.example.markdown_demo.common.lang;

import java.util.HashMap;
import java.util.Map;

public enum ResultType {
    SUCCESS("200", "成功"),

    NOT_FOUND("404", "资源不存在"),
    INVALID_REQUEST_BODY("405", "请求体不合法"),
    PATH_NOT_FOUND("406", "查询路径不存在"),
    RESOURCE_NOT_FOUND("407", "没有找到匹配资源"),
    NO_PERMISSION("408", "没有操作权限"),


    INTERNAL_SERVER_ERROR("500", "服务器内部错误"),
    UNAUTHORIZED("501", "token验证失败"),
    USERNAME_ALREADY_EXISTS("502", "用户名已被注册"),
    EMAIL_ALREADY_EXISTS("503", "邮箱已被注册"),
    INVALID_CREDENTIALS("504", "用户名或密码错误");

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