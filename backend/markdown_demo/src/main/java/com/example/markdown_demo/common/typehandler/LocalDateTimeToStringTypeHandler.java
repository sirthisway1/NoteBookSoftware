package com.example.markdown_demo.common.typehandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.TypeHandler;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocalDateTimeToStringTypeHandler implements TypeHandler<LocalDateTime> {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public void setParameter(PreparedStatement ps, int i, LocalDateTime parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, parameter.format(formatter));
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, String columnName) throws SQLException {
        String dateString = rs.getString(columnName);
        return dateString == null ? null : LocalDateTime.parse(dateString, formatter);
    }

    @Override
    public LocalDateTime getResult(ResultSet rs, int columnIndex) throws SQLException {
        String dateString = rs.getString(columnIndex);
        return dateString == null ? null : LocalDateTime.parse(dateString, formatter);
    }

    @Override
    public LocalDateTime getResult(CallableStatement cs, int columnIndex) throws SQLException {
        String dateString = cs.getString(columnIndex);
        return dateString == null ? null : LocalDateTime.parse(dateString, formatter);
    }
}
