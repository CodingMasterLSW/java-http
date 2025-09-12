package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.Cookie;

public class HttpRequestHeader {

    private final Map<String, String> httpRequestHeader;

    public HttpRequestHeader(final Map<String, String> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
    }

    public static HttpRequestHeader createFrom(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            int idx = headerLine.indexOf(":");
            String key = headerLine.substring(0, idx).trim();
            String value = headerLine.substring(idx + 1).trim();
            headers.put(key, value);
        }
        return new HttpRequestHeader(headers);
    }

    public String getContentLength() {
        return httpRequestHeader.getOrDefault("Content-Length", "0");
    }

    public boolean hasSessionId() {
        if (httpRequestHeader.containsKey("Cookie")) {
            return Cookie.hasCookieValue("JSESSIONID");
        }
        return false;
    }

    public String getSessionId() {
        if (httpRequestHeader.containsKey("Cookie")) {
            return Cookie.getCookieValue("JSESSIONID");
        }
        return null;
    }
}
