package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.HomeController;
import org.apache.coyote.http11.controller.LoginController;
import org.apache.coyote.http11.controller.RegisterController;
import org.apache.coyote.http11.controller.StaticResourceController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.service.HttpService;

public class DispatcherServlet {

    private final Map<String, Controller> handlerMapping = new HashMap<>();
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final StaticResourceController staticResourceController = new StaticResourceController();

    public DispatcherServlet() {
        HttpService httpService = new HttpService(sessionManager);
        handlerMapping.put("/login", new LoginController(httpService));
        handlerMapping.put("/register", new RegisterController(httpService));

        handlerMapping.put("/", new HomeController());
        handlerMapping.put("/index.html", new HomeController());
    }

    public HttpResponse service(HttpRequest request) {
        final RequestUri requestUri = request.getHttpRequestLine().getRequestUri();
        final String requestUriValue = requestUri.getValue();

        if (isStaticResource(requestUriValue)) {
            return staticResourceController.service(request);
        }

        final Controller controller = handlerMapping.get(requestUriValue);
        if (controller == null) {

            HttpResponseBody responseBody = new HttpResponseBody("/404.html".getBytes());
            HttpResponseHeader responseHeader = new HttpResponseHeader(requestUri.getValue(),
                    responseBody.getValue());
            return new HttpResponse(responseBody, HttpStatus.NOT_FOUND, responseHeader);
        }

        return controller.service(request);
    }

    private boolean isStaticResource(final String value) {
        return value.endsWith(".css") || value.endsWith(".html") || value.endsWith(".js");
    }

}
