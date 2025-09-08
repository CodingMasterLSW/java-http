package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public class HttpRequestHeader {

    private final Map<String, String> httpRequestHeader;

    public HttpRequestHeader(final Map<String, String> httpRequestHeader) {
        this.httpRequestHeader = httpRequestHeader;
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

    public Map<String, String> getHttpRequestHeaders() {
        return Collections.unmodifiableMap(httpRequestHeader);
    }
}
