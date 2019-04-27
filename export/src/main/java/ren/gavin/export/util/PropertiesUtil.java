package ren.gavin.export.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PropertiesUtil {

    private final static Map<Integer, Properties> PROPERTIES_POOL = new HashMap<>();

    public static Properties getProperties(String resource) throws IOException {
        return getProperties(null, resource, true);
    }

    public static Properties getProperties(String resource, boolean isCache) throws IOException {
        return getProperties(null, resource, isCache);
    }

    public static Properties getProperties(ClassLoader classLoader, String resource) throws IOException {
        return getProperties(classLoader, resource, true);
    }

    public static Properties getProperties(ClassLoader classLoader, String resource, boolean isCache) throws IOException {
        if (!isCache) {
            Properties properties = new Properties();
            try (InputStream is = Resources.getResourceAsStream(classLoader, resource)) {
                properties.load(is);
            }
            return properties;
        }

        Properties properties = PROPERTIES_POOL.get(resource.hashCode());
        if (null == properties) {
            synchronized (PROPERTIES_POOL) {
                properties = PROPERTIES_POOL.get(resource.hashCode());
                if (null == properties) {
                    properties = new Properties();
                    try (InputStream is = Resources.getResourceAsStream(classLoader, resource)) {
                        properties.load(is);
                    }
                    PROPERTIES_POOL.put(resource.hashCode(), properties);
                }
            }
        }
        return properties;
    }

    private PropertiesUtil() {
        throw new UnsupportedOperationException();
    }
}
