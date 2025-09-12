package org.apache.coyote.http11.response;

public class HttpResponseBody {

    private final byte[] value;

    public HttpResponseBody(final byte[] value) {
        this.value = value;
    }

    public byte[] getValue() {
        return value;
    }
}
