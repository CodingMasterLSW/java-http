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
            addCookie(keyValue[0], keyValue[1]);
        }
    }

    private static void addCookie(String key, String value) {
        cookie.put(key, value);
    }
}
