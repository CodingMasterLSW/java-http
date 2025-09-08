package org.apache.coyote.http11;

import java.util.Collections;
import java.util.Map;

public class HttpRequestHeaders {

    private final Map<String, String> httpRequestHeaders;

    public HttpRequestHeaders(final Map<String, String> httpRequestHeaders) {
        this.httpRequestHeaders = httpRequestHeaders;
    }

    public String getContentLength() {
        return httpRequestHeaders.getOrDefault("Content-Length", "0");
    }

    public Map<String, String> getHttpRequestHeaders() {
        return Collections.unmodifiableMap(httpRequestHeaders);
    }
}
