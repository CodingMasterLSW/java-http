package org.apache.coyote.http11.controller;

import java.util.Optional;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.request.RequestUri;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.response.ResponseData;
import org.apache.coyote.http11.response.ResponseGenerator;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.service.LoginService;

public class LoginController extends AbstractController {

    private final LoginService loginService;
    private final SessionManager sessionManager = SessionManager.getInstance();
    private final ResponseGenerator responseGenerator = new ResponseGenerator();

    public LoginController(final LoginService loginService) {
        this.loginService = loginService;
    }

    @Override
    protected HttpResponse doPost(final HttpRequest request) {
        final RequestUri requestUri = request.getHttpRequestLine().getRequestUri();
        final RequestUri htmlRequestUri = requestUri.convertHtmlFromUri();

        boolean loginStatus = loginService.login(request.getRequestBody());

        if (loginStatus) {
            return loginProcess(htmlRequestUri);
        }
        return invalidLoginProcess();
    }

    @Override
    protected HttpResponse doGet(final HttpRequest request) {
        return getLoginPage(request);
    }

    public HttpResponse getLoginPage(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequest.hasSessionId()) {
            return existSessionIdProcess(httpRequest, httpRequestLine);
        }
        return notExistSessionIdProcess(httpRequest);
    }

    private HttpResponse loginProcess(final RequestUri htmlRequestUri) {
        final ResponseData responseData = responseGenerator.calculateByteFromStaticResource(
                htmlRequestUri.getValue());

        HttpResponseBody responseBody = new HttpResponseBody(responseData.value());
        HttpResponseHeader responseHeader = new HttpResponseHeader(htmlRequestUri.getValue(), responseBody.getValue());

        if (responseData.isExistFile()) {
            responseHeader.addHeader("Location", "/index.html");
            return new HttpResponse(responseBody, HttpStatus.FOUND, responseHeader);
        }
        return new HttpResponse(responseBody, HttpStatus.NOT_FOUND, responseHeader);
    }

    private static HttpResponse invalidLoginProcess() {
        HttpResponseBody responseBody = new HttpResponseBody("/401.html".getBytes());
        HttpResponseHeader responseHeader = new HttpResponseHeader("/401.html", responseBody.getValue());
        return new HttpResponse(responseBody, HttpStatus.UNAUTHORIZED, responseHeader);
    }

    private HttpResponse notExistSessionIdProcess(HttpRequest request) {
        final RequestUri requestUri = request.getHttpRequestLine().getRequestUri();
        final RequestUri htmlRequestUri = requestUri.convertHtmlFromUri();

        final ResponseData responseData = responseGenerator.calculateByteFromStaticResource(
                htmlRequestUri.getValue());

        HttpResponseBody responseBody = new HttpResponseBody(responseData.value());
        HttpResponseHeader responseHeader = new HttpResponseHeader(
                htmlRequestUri.getValue(),
                responseBody.getValue()
        );

        if (responseData.isExistFile()) {
            return new HttpResponse(responseBody, HttpStatus.SUCCESS, responseHeader);
        }
        return new HttpResponse(responseBody, HttpStatus.NOT_FOUND, responseHeader);
    }

    private HttpResponse existSessionIdProcess(
            final HttpRequest httpRequest,
            final HttpRequestLine httpRequestLine
    ) {
        final String sessionId = httpRequest.getSessionId();
        final Optional<Session> session = sessionManager.findSession(sessionId);
        if (session.isEmpty()) {
            return notExistSessionIdProcess(httpRequest);
        }

        final RequestUri requestUri = httpRequestLine.getRequestUri();
        final RequestUri htmlRequestUri = requestUri.convertHtmlFromUri();

        return loginProcess(htmlRequestUri);
    }
}
