package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.service.HttpService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final HttpService httpService;

    public RegisterController(final HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        httpService.saveRegister(request);

        HttpResponseBody body = new HttpResponseBody(new byte[0]);
        HttpResponseHeader header = new HttpResponseHeader(
                request.getHttpRequestLine().getRequestUri().getValue(),
                body.getValue()
        );
        header.addHeader("Location", "/index.html");
        return new HttpResponse(body, HttpStatus.FOUND, header);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return httpService.getRegisterPage(request);
    }
}
