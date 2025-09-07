package org.apache.coyote.http11;

public class HttpResponse {

    private final byte[] body;
    private final HttpStatus httpStatus;

    public HttpResponse(byte[] body, HttpStatus httpStatus) {
        this.body = body;
        this.httpStatus = httpStatus;
    }

    public byte[] getBody() {
        return body;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
