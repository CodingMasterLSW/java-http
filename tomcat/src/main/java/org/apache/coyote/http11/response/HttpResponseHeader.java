package org.apache.coyote.http11.response;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HttpResponseHeader {

    private static final String HTML_TYPE = "text/html;";
    private static final String CSS_TYPE = "text/css;";
    private static final String JS_TYPE = "application/javascript;";
    private static final String CHARSET = "charset=utf-8";

    private final Map<String, String> responseHeader;

    public HttpResponseHeader(final String requestUri, final byte[] body) {
        this.responseHeader = new LinkedHashMap<>();
        parseContentType(requestUri);
        putContentLength(body);
    }

    public void addSessionId() {
        responseHeader.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID().toString());
    }

    private String parseContentType(final String requestUri) {
        String contentType = "Content-Type";

        if (requestUri.endsWith(".html")) {
            responseHeader.put(contentType, HTML_TYPE + CHARSET);
        }

        if (requestUri.endsWith(".css")) {
           responseHeader.put(contentType, CSS_TYPE + CHARSET);
        }

        if (requestUri.endsWith(".js")) {
            responseHeader.put(contentType, JS_TYPE + CHARSET);
        }
        return null;
    }

    private void putContentLength(byte[] body) {
        if (body == null) {
            responseHeader.put("Content-Length", "0");
        }
        responseHeader.put("Content-Length", String.valueOf(body.length));
    }

    public String parseAndGetAllHeader() {
        StringBuilder sb = new StringBuilder();
        for (String key : responseHeader.keySet()) {
            sb.append(key).append(": ").append(responseHeader.get(key)).append(" \r\n");
        }
        sb.append("\r\n");
        return sb.toString();
    }
}
