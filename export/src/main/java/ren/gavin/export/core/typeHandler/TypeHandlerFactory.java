package ren.gavin.export.core.typeHandler;

import ren.gavin.export.core.ExportException;
import ren.gavin.export.util.Resources;
import ren.gavin.export.util.Validate;

import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class TypeHandlerFactory {

    private static final String LOAD_PATH = "ren.gavin.export.core.typeHandler";

    private Map<Integer, TypeHandler> typeHandlers = new HashMap<>();

    private TypeHandler defaultTypeHandler = new DefaultTypeHandler();

    private TypeHandlerFactory() {
        String path = LOAD_PATH.replaceAll("\\.", "/");
        ClassLoader loader = TypeHandlerFactory.class.getClassLoader();
        try {
            File dir = Resources.getResourceAsFile(loader, path);
            File[] files = dir.listFiles(f -> f.getName().endsWith(".class"));
            Validate.requireNonNull(files);
            for (File file : files) {
                String fileName = file.getName();
                Class<?> clazz = loader.loadClass(LOAD_PATH + "." + fileName.substring(0, fileName.lastIndexOf(".class")));
                if (TypeHandler.class.isAssignableFrom(clazz) && clazz != TypeHandler.class && clazz != DefaultTypeHandler.class) {
                    TypeHandler typeHandler = (TypeHandler)clazz.newInstance();
                    typeHandlers.put(typeHandler.getType(), typeHandler);
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException | IOException e) {
            throw new ExportException("加载类型处理器失败", e);
        }
    }

    public TypeHandler getTypeHandler(int type) {
        TypeHandler typeHandler = typeHandlers.get(type);
        return typeHandler == null ? defaultTypeHandler : typeHandler;
    }

    public TypeHandler getDefaultTypeHandler() {
        return defaultTypeHandler;
    }

    public static TypeHandlerFactory getInstance() {
        return InstanceHolder.instance;
    }

    private static class InstanceHolder {
        static TypeHandlerFactory instance = new TypeHandlerFactory();
    }

    public static class DefaultTypeHandler implements TypeHandler<Object> {
        @Override
        public int getType() {
            return Integer.MIN_VALUE;
        }

        @Override
        public Object getResult(ResultSet resultSet, String columnName) throws SQLException {
            return resultSet.getObject(columnName);
        }

        @Override
        public Object getResult(ResultSet resultSet, int columnIndex) throws SQLException {
            return resultSet.getObject(columnIndex);
        }
    }
}
