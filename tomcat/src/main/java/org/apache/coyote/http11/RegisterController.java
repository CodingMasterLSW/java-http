package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class RegisterController extends AbstractController {

    private final HttpService httpService;

    public RegisterController(final HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        return httpService.saveRegister(request);
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return httpService.getRegisterPage(request);
    }
}
