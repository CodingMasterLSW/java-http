package org.apache.coyote.http11.service;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.response.HttpStatus;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestLine;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class HttpService {

    private final SessionManager sessionManager;

    public HttpService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpResponse getLoginPage(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequest.hasSessionId()) {
            final String sessionId = httpRequest.getSessionId();
            final Optional<Session> session = sessionManager.findSession(sessionId);
            if (session.isPresent()) {
                final HttpResponseBody httpResponseBody = new HttpResponseBody(
                        httpRequestLine.getRequestUri().getValue() + ".html");

                final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                        httpRequestLine.getRequestUri().getValue() + ".html",
                        httpResponseBody.getValue()
                );

                httpResponseHeader.addHeader("Location", "/index.html");

                return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS,
                        httpResponseHeader);
            }
        }

        final HttpResponseBody httpResponseBody = new HttpResponseBody(
                httpRequestLine.getRequestUri().getValue() + ".html");

        final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                httpRequestLine.getRequestUri().getValue() + ".html",
                httpResponseBody.getValue()
        );

        return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS, httpResponseHeader);
    }

    public HttpResponse login(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        final String requestBody = httpRequest.getRequestBody();

        Map<String, String> requestValues = new HashMap<>();
        final String[] firstSplit = requestBody.split("&");
        for (String s : firstSplit) {
            final String[] keyAndValue = s.split("=");
            requestValues.put(keyAndValue[0], keyAndValue[1]);
        }

        if (isExistUser(requestValues)) {

            final HttpResponseBody httpResponseBody = new HttpResponseBody(
                    httpRequestLine.getRequestUri().getValue() + ".html");

            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                    httpRequestLine.getRequestUri().getValue() + ".html",
                    httpResponseBody.getValue()
            );

            httpResponseHeader.addHeader("Location", "/index.html");

            if (!httpRequest.hasSessionId()) {
                httpResponseHeader.addSessionId();
            }

            return new HttpResponse(httpResponseBody, HttpStatus.FOUND, httpResponseHeader);
        }

        final HttpResponseBody httpResponseBody = new HttpResponseBody(
                "/401.html");

        final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                "/401.html",
                httpResponseBody.getValue()
        );

        return new HttpResponse(httpResponseBody, HttpStatus.UNAUTHORIZED, httpResponseHeader);
    }

    private boolean isExistUser(final Map<String, String> queryParams) {
        try {
            final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("이런 유저는 없답니다."));
            if (user.checkPassword(queryParams.get("password"))) {
                Session session = new Session(UUID.randomUUID().toString());
                session.setAttribute("user", user);
                sessionManager.add(session);
                Cookie.addCookie("JSESSIONID", session.getId());

                log.info(user.toString());
                return true;
            }
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
            return false;
        }
        return false;
    }
}
