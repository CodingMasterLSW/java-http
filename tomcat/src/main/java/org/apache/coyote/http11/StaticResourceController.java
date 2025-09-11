package org.apache.coyote.http11;

import java.net.URL;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class StaticResourceController extends AbstractController {

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        // 정적 자원은 Post가 존재하지 않는다.
        return null;
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        String uri = request.getHttpRequestLine().getRequestUri().getValue();
        URL resource = getClass().getClassLoader().getResource("static" + uri);
        if (resource == null) {
            new HttpResponse(
                    new HttpResponseBody("/404.html"),
                    HttpStatus.NOT_FOUND,
                    new HttpResponseHeader("/404.html", "/404.html".getBytes()));
        }
        final HttpResponseBody httpResponseBody = new HttpResponseBody(uri);
        final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(uri,
                httpResponseBody.calculateBytes(uri));
        return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS, httpResponseHeader);
    }
}
