package org.apache.coyote.http11;

import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class DispatcherServlet {

    private final Map<String, Controller> handlerMapping = new HashMap<>();
    private final SessionManager sessionManager = SessionManager.getInstance();

    public DispatcherServlet() {
        HttpService httpService = new HttpService(sessionManager);
        handlerMapping.put("/login", new LoginController(httpService));
        handlerMapping.put("/register", new RegisterController(httpService));
        handlerMapping.put("/", new HomeController(httpService));
    }

    public HttpResponse service(HttpRequest request) {
        final String requestUriValue = request.getHttpRequestLine().getRequestUri().getValue();
        final Controller controller = handlerMapping.get(requestUriValue);
        if (controller == null) {
            final HttpResponseBody responseBody = new HttpResponseBody("/404.html");
            return new HttpResponse(responseBody, HttpStatus.NOT_FOUND, new HttpResponseHeader("/404.html", responseBody.getValue()));
        }
        return controller.service(request);
    }

}
