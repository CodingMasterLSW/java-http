package org.apache.coyote.http11.request;

import java.io.BufferedReader;
import java.io.IOException;

public class HttpRequest {

    private final HttpRequestLine httpRequestLine;
    private final HttpRequestHeader requestHeader;
    private final String requestBody;

    public HttpRequest(
            final HttpRequestLine httpRequestLine,
            final HttpRequestHeader requestHeader,
            final BufferedReader br
    ) throws IOException {
        this.httpRequestLine = httpRequestLine;
        this.requestHeader = requestHeader;
        this.requestBody = parseRequestBody(requestHeader, br);
    }

    public HttpRequestLine getHttpRequestLine() {
        return httpRequestLine;
    }

    public String getRequestBody() {
        return requestBody;
    }

    public boolean hasSessionId() {
        return requestHeader.hasSessionId();
    }

    public String getSessionId() {
        return requestHeader.getSessionId();
    }

    public HttpRequestHeader getRequestHeader() {
        return requestHeader;
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
