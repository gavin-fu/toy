package ren.gavin.export.core.typeHandler;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface TypeHandler<T> {

    int getType();

    T getResult(ResultSet resultSet, String columnName) throws SQLException;

    T getResult(ResultSet resultSet, int columnIndex) throws SQLException;
}
