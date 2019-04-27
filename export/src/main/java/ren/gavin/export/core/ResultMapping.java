package ren.gavin.export.core;

import ren.gavin.export.core.typeHandler.TypeHandler;
import ren.gavin.export.core.typeHandler.TypeHandlerFactory;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;

public class ResultMapping implements Iterable<Map<String, Object>> {

    private String id;

    private ResultSet resultSet;

    private int rowNumber;

    private int columnCount;

    private String[] columnLabel;

    private TypeHandler[] typeHandlers;

    public ResultMapping(String id, ResultSet resultSet) throws SQLException {
        TypeHandlerFactory factory = TypeHandlerFactory.getInstance();
        ResultSetMetaData metaData = resultSet.getMetaData();
        this.columnCount = metaData.getColumnCount();
        this.columnLabel = new String[columnCount];
        this.typeHandlers = new TypeHandler[columnCount];
        for (int i = 0; i < columnCount; i++) {
            TypeHandler typeHandler = factory.getTypeHandler(metaData.getColumnType(i + 1));
            typeHandlers[i] = typeHandler == null ? factory.getDefaultTypeHandler() : typeHandler;
            columnLabel[i] = metaData.getColumnLabel(i + 1).toUpperCase();
        }
        this.resultSet = resultSet;
        this.id = id;
    }

    public List<Map<String, Object>> getResult() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (Map<String, Object> row : this) {
            result.add(row);
        }
        return result;
    }

    @Override
    public Iterator<Map<String, Object>> iterator() {
        return new Iterator<Map<String, Object>>() {
            @Override
            public boolean hasNext() {
                try {
                    rowNumber++;
                    return resultSet.next();
                } catch (SQLException e) {
                    throw new ExportException("获取"+ id + "节点结果集 第 " + rowNumber + "行失败", e);
                }
            }

            @Override
            public Map<String, Object> next() {
                Map<String, Object> result = new HashMap<>();
                try {
                    for (int i = 0; i < columnCount; i++) {
                        result.put(columnLabel[i], typeHandlers[i].getResult(resultSet, i + 1));
                    }
                } catch (SQLException e) {
                    throw new ExportException("获取" + id + "节点结果集 第 " + rowNumber + "行， 第 " + (i + 1) + "列失败" , e);
                }
                return result;
            }
        };
    }

    public boolean hasColumnLabel(String label) {
        if (null != label) {
            for (String c : columnLabel) {
                if (Objects.equals(c, label)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void close() {
        if (null != resultSet) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
