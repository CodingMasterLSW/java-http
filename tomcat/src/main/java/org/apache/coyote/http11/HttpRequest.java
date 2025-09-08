package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeader requestHeaders;
    private final String requestBody;

    public HttpRequest(
            final HttpRequestLine httpRequestLine,
            final HttpRequestHeader requestHeaders,
            final BufferedReader br
    ) throws IOException {
        this.httpRequestLine = httpRequestLine;
        this.requestHeaders = requestHeaders;
        this.requestBody = parseRequestBody(requestHeaders, br);
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public HttpRequestHeader getRequestHeaders() {
        return requestHeaders;
    }

    private String parseRequestBody(final HttpRequestHeader requestHeaders, final BufferedReader br)
            throws IOException {
        int contentLength = Integer.parseInt(requestHeaders.getContentLength());
        if (contentLength > 0) {
            char[] buffer = new char[contentLength];
            br.read(buffer, 0, contentLength);
            return new String(buffer);
        }
        return null;
    }
}
