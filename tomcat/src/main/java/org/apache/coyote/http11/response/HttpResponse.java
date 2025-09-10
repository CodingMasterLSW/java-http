package org.apache.coyote.http11.response;

import java.io.IOException;
import java.io.OutputStream;
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

    public void write(final OutputStream outputStream) throws IOException {
        outputStream.write((getResponseLine() + " \r\n").getBytes());
        outputStream.write(responseHeader.parseAndGetAllHeader().getBytes());
        outputStream.write(responseBody.getValue());
        outputStream.flush();
    }

    public HttpResponseHeader getResponseHeader() {
        return responseHeader;
    }

    private String getResponseLine() {
        return "HTTP/1.1" + " " + httpStatus.getCode() + " " + httpStatus.getMessage();
    }
}
