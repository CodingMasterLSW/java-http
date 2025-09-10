package org.apache.coyote.http11;

import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;

public class HomeController extends AbstractController {

    private final HttpService httpService;

    public HomeController(final HttpService httpService) {
        this.httpService = httpService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        // 아직 기능 존재X
        return null;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return httpService.indexProcess(request.getHttpRequestLine());
    }
}
