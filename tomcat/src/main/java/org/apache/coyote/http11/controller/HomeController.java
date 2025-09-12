package org.apache.coyote.http11.controller;

import java.nio.charset.StandardCharsets;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpStatus;

public class HomeController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        // 아직 기능 존재X
        return null;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return indexProcess(request.getHttpRequestLine());
    }

    private HttpResponse indexProcess(HttpRequestLine httpRequestLine) {
        final RequestUri requestUri = httpRequestLine.getRequestUri();

        HttpResponseBody responseBody = new HttpResponseBody("Hello world!".getBytes(
                StandardCharsets.UTF_8));
        HttpResponseHeader responseHeader = new HttpResponseHeader(requestUri.getValue(), responseBody.getValue());
        return new HttpResponse(responseBody, HttpStatus.SUCCESS, responseHeader);
    }

}
