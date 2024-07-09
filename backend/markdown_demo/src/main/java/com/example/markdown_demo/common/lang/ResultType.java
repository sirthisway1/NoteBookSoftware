package com.example.markdown_demo.common.lang;

import java.util.HashMap;
import java.util.Map;

public enum ResultType {
    SUCCESS("200", "成功"),

    INVALID_REQUEST_BODY("400", "请求体不合法"),
    UNAUTHORIZED("401", "token验证失败"),
    INVALID_CREDENTIALS("401", "用户名或密码错误"),
    NO_PERMISSION("403", "没有操作权限"),
    NOT_FOUND("404", "资源不存在"),
    PATH_NOT_FOUND("404", "查询路径不存在"),
    USERNAME_ALREADY_EXISTS("409", "用户名已被注册"),
    EMAIL_ALREADY_EXISTS("409", "邮箱已被注册"),


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
    public Map<String, Object> asMap(String key, Object value) {
        Map<String, Object> map = new HashMap<>();
        map.put("code", code);
        map.put("message", message);
        map.put(key, value);
        return map;
    }


}






