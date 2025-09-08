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

    public Map<String, String> getHttpRequestHeaders() {
        return Collections.unmodifiableMap(httpRequestHeader);
    }
}
