package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseData;
import org.apache.coyote.http11.response.ResponseGenerator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.RegisterService;

public class RegisterController extends AbstractController {

    private final ResponseGenerator responseGenerator = new ResponseGenerator();
    private final RegisterService registerService;

    public RegisterController(final RegisterService registerService) {
        this.registerService = registerService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final RequestUri requestUri = request.getHttpRequestLine().getRequestUri();
        registerService.saveRegister(request.getRequestBody());

        HttpResponseBody body = new HttpResponseBody(new byte[0]);
        HttpResponseHeader header = new HttpResponseHeader(requestUri.getValue(), body.getValue());
        header.addHeader("Location", "/index.html");

        return new HttpResponse(body, HttpStatus.FOUND, header);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return getRegisterPage(request);
    }

    private HttpResponse getRegisterPage(HttpRequest httpRequest) {
        final RequestUri requestUri = httpRequest.getHttpRequestLine().getRequestUri();
        final RequestUri htmlRequestUri = requestUri.convertHtmlFromUri();

        final ResponseData responseData = responseGenerator.calculateByteFromStaticResource(
                htmlRequestUri.getValue());

        final HttpResponseBody responseBody = new HttpResponseBody(responseData.value());
        final HttpResponseHeader responseHeader = new HttpResponseHeader(htmlRequestUri.getValue(),
                responseBody.getValue());

        return new HttpResponse(responseBody, HttpStatus.SUCCESS, responseHeader);
    }
}
