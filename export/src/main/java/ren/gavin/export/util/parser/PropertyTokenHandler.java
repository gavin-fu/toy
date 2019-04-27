package ren.gavin.export.util.parser;

import java.util.Properties;

public class PropertyTokenHandler implements TokenHandler {

    private Properties properties;

    public PropertyTokenHandler(Properties properties) {
        this.properties = properties;
    }

    @Override
    public String handleToken(String key) {
        if (null != properties) {
            if (properties.containsKey(key)) {
                return properties.getProperty(key);
            }
        }
        return "${" + key + "}";
    }
}
