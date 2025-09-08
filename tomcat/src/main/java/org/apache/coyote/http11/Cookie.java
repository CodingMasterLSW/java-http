package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;

public class Cookie {

    private final Map<String, String> cookie = new HashMap<>();

    public void addAllCookie(String value) {
        //TODO : cookie 값 확인하고 파싱 후 저장하기
    }

    public void addCookie(String key, String value) {
        cookie.put(key, value);
    }

}
