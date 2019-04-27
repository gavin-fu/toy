package ren.gavin.export.util;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Properties;

public class Resources {

    public static File getResourceAsFile(String resource) throws IOException {
        return getResourceAsFile(null, resource);
    }

    public static File getResourceAsFile(ClassLoader classLoader, String resource) throws IOException {
        return new File(getResourceAsUrl(classLoader, resource).getFile());
    }

    public static URL getResourceAsUrl(String resource) throws IOException{
        return getResourceAsUrl(null, resource);
    }

    public static URL getResourceAsUrl(ClassLoader classLoader, String resource) throws IOException {
        for (ClassLoader loader : getClassLoader(classLoader)) {
            if (null != loader) {
                URL url = loader.getResource(resource);
                if (null == url) {
                    url = loader.getResource("/" + resource);
                }
                if (null != url) {
                    return url;
                }
            }
        }
        throw new IOException("Could not found resource " + resource);
    }

    public static Properties getResourceAsProperties(String resource) throws IOException {
        return getResourceAsProperties(null, resource);
    }

    public static Properties getResourceAsProperties(ClassLoader classLoader, String resource) throws IOException {
        Properties properties = new Properties();
        InputStream is = getResourceAsStream(classLoader, resource);
        properties.load(is);
        return properties;
    }

    public static Reader getResourceAsReader(String resource) throws IOException {
        return getResourceAsReader(null, null, resource);
    }

    public static Reader getResourceAsReader(Charset charset, String resource) throws IOException {
        return getResourceAsReader(null, charset, resource);
    }

    public static Reader getResourceAsReader(ClassLoader classLoader, String resource) throws IOException {
        return getResourceAsReader(classLoader, null, resource);
    }

    public static Reader getResourceAsReader(ClassLoader classLoader, Charset charset, String resource) throws IOException {
        return new InputStreamReader(getResourceAsStream(classLoader, resource), charset == null ? Charset.forName("UTF-8") : charset);
    }

    public static InputStream getResourceAsStream(String resource) throws IOException {
        return getResourceAsStream(null, resource);
    }

    public static InputStream getResourceAsStream(ClassLoader classLoader, String resource) throws IOException {
        for (ClassLoader loader : getClassLoader(classLoader)) {
            if (null != loader) {
                InputStream is = loader.getResourceAsStream(resource);
                if (null == is) {
                    is = loader.getResourceAsStream("/" + resource);
                }
                if (null != is) {
                    return is;
                }
            }
        }
        throw new IOException("Could not found resource " + resource);
    }


    private static ClassLoader[] getClassLoader(ClassLoader classLoader) {
        return new ClassLoader[] {
                classLoader,
                Thread.currentThread().getContextClassLoader(),
                Object.class.getClassLoader(),
                ClassLoader.getSystemClassLoader()
        };
    }

    private Resources() {
    }
}
