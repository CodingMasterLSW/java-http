package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class DispatcherServlet {

    private final Map<String, Controller> handlerMapping = new HashMap<>();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final StaticResourceController staticResourceController = new StaticResourceController();

    public DispatcherServlet() {
        HttpService httpService = new HttpService(sessionManager);
        handlerMapping.put("/login", new LoginController(httpService));
        handlerMapping.put("/register", new RegisterController(httpService));

        handlerMapping.put("/", new HomeController(httpService));
        handlerMapping.put("/index.html", new HomeController(httpService));
    }

    public HttpResponse service(HttpRequest request) {
        final RequestUri requestUri = request.getHttpRequestLine().getRequestUri();
        final String requestUriValue = requestUri.getValue();

        if (isStaticResource(requestUriValue)) {
            return staticResourceController.service(request);
        }

        final Controller controller = handlerMapping.get(requestUriValue);
        if (controller == null) {
            final HttpResponseBody responseBody = new HttpResponseBody("/404.html");
            return new HttpResponse(responseBody, HttpStatus.NOT_FOUND,
                    new HttpResponseHeader("/404.html", responseBody.getValue()));
        }
        return controller.service(request);
    }

    private boolean isStaticResource(final String value) {
        return value.endsWith(".css") || value.endsWith(".html") || value.endsWith(".js");
    }

}
