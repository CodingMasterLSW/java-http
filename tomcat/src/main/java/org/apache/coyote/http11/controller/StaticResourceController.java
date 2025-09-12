package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.ResponseData;
import org.apache.coyote.http11.response.ResponseGenerator;

public class StaticResourceController extends AbstractController {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        // 정적 자원은 Post가 존재하지 않는다.
        return null;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        String uri = request.getHttpRequestLine().getRequestUri().getValue();
        final ResponseData responseData = responseGenerator.calculateByteFromStaticResource(uri);
        final HttpResponseBody responseBody = new HttpResponseBody(responseData.value());
        final HttpResponseHeader responseHeader = new HttpResponseHeader(
                uri, responseBody.getValue());

        if (!responseData.isExistFile()) {
            return new HttpResponse(responseBody, HttpStatus.NOT_FOUND, responseHeader);
        }
        return new HttpResponse(responseBody, HttpStatus.SUCCESS, responseHeader);
    }
}
