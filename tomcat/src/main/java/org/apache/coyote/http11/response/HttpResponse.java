package org.apache.coyote.http11.response;

import org.apache.coyote.http11.HttpStatus;

public class HttpResponse {

    private final HttpResponseBody responseBody;
    private final HttpStatus httpStatus;
    private final HttpResponseHeader responseHeader;

    public HttpResponse(
            HttpResponseBody responseBody,
            HttpStatus httpStatus,
            HttpResponseHeader responseHeader
    ) {
        this.responseBody = responseBody;
        this.httpStatus = httpStatus;
        this.responseHeader = responseHeader;
    }

    public byte[] getBodyValue() {
        return responseBody.getValue();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public HttpResponseBody getResponseBody() {
        return responseBody;
    }

    public HttpResponseHeader getResponseHeader() {
        return responseHeader;
    }

    public String getResponseLine() {
        return "HTTP/1.1" + " " + httpStatus.getCode() + " " + httpStatus.getMessage();
    }
}
