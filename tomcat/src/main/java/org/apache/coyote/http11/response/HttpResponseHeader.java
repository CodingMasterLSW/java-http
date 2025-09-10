package org.apache.coyote.http11.response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class HttpResponseHeader {

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

    public void addHeader(String key, String value) {
        responseHeader.put(key, value);
    }

    private void parseContentType(final String requestUri) {
        String contentType = "Content-Type";

        Path path = Paths.get(requestUri);
        try {
            String mineType = Files.probeContentType(path);
            if (mineType.startsWith("text/") || mineType.equals("application/javascript")) {
                responseHeader.put(contentType, mineType + ";" + CHARSET);
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("잘못된 파일 확장자명입니다.");
        }
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
