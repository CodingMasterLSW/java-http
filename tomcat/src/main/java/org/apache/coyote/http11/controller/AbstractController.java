package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public abstract class AbstractController implements Controller {

    @Override
    public HttpResponse service(final HttpRequest request) {
        if (request.getHttpRequestLine().getMethod().equals("POST")) {
            return doPost(request);
        }
        return doGet(request);
    }

    protected abstract HttpResponse doPost(HttpRequest request);

    protected abstract HttpResponse doGet(HttpRequest request);

}
