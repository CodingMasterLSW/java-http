package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private static final Map<String, String> cookie = new HashMap<>();

    private Cookie() {}

    public static void addAllCookie(String value) {
        if (value == null) {
            return;
        }

        final String[] split = value.split(";");
        for (String s : split) {
            String[] keyValue = s.trim().split("=");
            if (keyValue.length != 2) {
                return;
            }
            addCookie(keyValue[0], keyValue[1]);
        }
    }

    public static String getCookieValue(String value) {
        return cookie.get(value);
    }

    public static boolean hasCookieValue(String value) {
        return cookie.containsKey(value);
    }

    public static void addCookie(String key, String value) {
        cookie.put(key, value);
    }
}
