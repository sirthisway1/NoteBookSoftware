package com.example.markdown_demo.common.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ListToStringTypeHandler extends BaseTypeHandler<List<String>> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List<String> parameter, JdbcType jdbcType) throws SQLException {
        String value = parameter.stream()
                .filter(tag -> tag != null && !tag.isEmpty()) // 过滤掉空标签
                .collect(Collectors.joining(","));
        ps.setString(i, value);
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        return value != null ? Arrays.stream(value.split(","))
                .filter(tag -> tag != null && !tag.isEmpty()) // 过滤掉空标签
                .collect(Collectors.toList()) : null;
    }

    @Override
    public List<String> getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String value = rs.getString(columnIndex);
        return value != null ? Arrays.stream(value.split(","))
                .filter(tag -> tag != null && !tag.isEmpty()) // 过滤掉空标签
                .collect(Collectors.toList()) : null;
    }

    @Override
    public List<String> getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String value = cs.getString(columnIndex);
        return value != null ? Arrays.stream(value.split(","))
                .filter(tag -> tag != null && !tag.isEmpty()) // 过滤掉空标签
                .collect(Collectors.toList()) : null;
    }
}