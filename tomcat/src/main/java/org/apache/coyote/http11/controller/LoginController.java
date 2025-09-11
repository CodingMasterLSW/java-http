package org.apache.coyote.http11.controller;

import org.apache.coyote.http11.service.HttpService;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class LoginController extends AbstractController {

    private final HttpService httpService;

    public LoginController(final HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        return httpService.login(request);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return httpService.getLoginPage(request);
    }
}
