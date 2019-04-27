package ren.gavin.export.util;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    private static final String DB_CONFIG = "datasource.properties";

    private static String url;

    private static String username;

    private static String password;

    static {
        try {
            Properties properties = PropertiesUtil.getProperties(DB_CONFIG);
            Class.forName(properties.getProperty("driver"));
            url = properties.getProperty("url");
            username = properties.getProperty("username");
            password = properties.getProperty("password");
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("Error Initializing DBUtil. Cause: " + e, e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url, username, password);
    }

    public static void closeConnection(Connection connection) {
        if (null != connection) {
            try {
                connection.close();
            } catch (SQLException e) {
                // ignore
            }
        }
    }
}
