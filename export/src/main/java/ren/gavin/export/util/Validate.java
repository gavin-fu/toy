package ren.gavin.export.util;

import java.util.Collection;
import java.util.Map;

public class Validate {

    public static void requireTrue(boolean flag) {
        requireTrue(flag, null);
    }

    public static void requireTrue(boolean flag, String msg) {
        if (!flag) {
            throw new IllegalStateException(msg);
        }
    }

    public static void requireNonNull(Object obj) {
        requireNonNull(obj, null);
    }

    public static void requireNonNull(Object obj, String msg) {
        if (obj == null) {
            throw new NullPointerException(msg);
        }
    }

    public static void requireNonEmpty(Object obj) {
        requireNonEmpty(obj, null);
    }

    public static void requireNonEmpty(Object obj, String msg) {
        requireNonNull(obj, msg);
        if (obj instanceof CharSequence){
            if (((CharSequence) obj).length() == 0) throw new IllegalArgumentException(msg);
        } else if (obj instanceof Collection) {
            if (((Collection) obj).size() == 0) throw new IllegalArgumentException(msg);
        } else if (obj instanceof Map) {
            if (((Map) obj).size() == 0) throw new IllegalArgumentException(msg);
        }
    }

    private Validate() {
    }
}
