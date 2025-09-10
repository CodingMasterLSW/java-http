package org.apache.coyote.http11.request;

public class HttpRequestLine {

    private final String method;
    private RequestUri requestUri;
    private final String version;

    public static HttpRequestLine createFrom(String line) {
        return new HttpRequestLine(line);
    }

    private HttpRequestLine(String line) {
        final String[] lines = line.split(" ");
        this.method = lines[0];
        this.requestUri = new RequestUri(lines[1]);
        this.version = lines[2];
    }

    public void modifyRequestUri(RequestUri requestUri) {
        this.requestUri = requestUri;
    }

    public String getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }

    public byte[] calculateBytes() {
        return this.requestUri.calculateBytes();
    }

}
