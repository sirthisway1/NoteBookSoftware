package com.example.markdown_demo.common.typehandler;

import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToStringTypeHandler implements TypeHandler<String> {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void setParameter(PreparedStatement ps, int i, String parameter, JdbcType jdbcType) throws SQLException {
        if (parameter != null) {
            LocalDateTime localDateTime = LocalDateTime.parse(parameter, formatter);
            ps.setObject(i, localDateTime);
        } else {
            ps.setObject(i, null);
        }
    }

    @Override
    public String getResult(ResultSet rs, String columnName) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnName, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }

    @Override
    public String getResult(ResultSet rs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = rs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }

    @Override
    public String getResult(CallableStatement cs, int columnIndex) throws SQLException {
        LocalDateTime localDateTime = cs.getObject(columnIndex, LocalDateTime.class);
        return localDateTime != null ? localDateTime.format(formatter) : null;
    }
}
