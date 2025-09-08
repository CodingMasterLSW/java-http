package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseBody;
import org.apache.coyote.http11.response.HttpResponseHeader;

public class HttpService {

    private final SessionManager sessionManager;

    public HttpService(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    public HttpResponse basicProcess(HttpRequestLine httpRequestLine) {

        final HttpResponseBody httpResponseBody = new HttpResponseBody(
                httpRequestLine.getRequestUri().getValue());
        final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                httpRequestLine.getRequestUri().getValue(),
                httpResponseBody.getValue()
        );

        if (!httpRequestLine.getRequestUri().isExistFile()) {
            return new HttpResponse(httpResponseBody, HttpStatus.NOT_FOUND, httpResponseHeader);
        }

        return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS, httpResponseHeader);
    }

    public HttpResponse registerProcess(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequestLine.getRequestUri().endWith("/register") &&
                httpRequestLine.getMethod().equals("GET")) {
            httpRequestLine.modifyRequestUri(httpRequestLine.getRequestUri().convertHtmlFromUri());
            final HttpResponseBody httpResponseBody = new HttpResponseBody(
                    httpRequestLine.getRequestUri().getValue());

            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                    httpRequestLine.getRequestUri().getValue(),
                    httpResponseBody.getValue()
            );

            return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS, httpResponseHeader);
        }

        if (httpRequestLine.getRequestUri().endWith("/register") &&
                httpRequestLine.getMethod().equals("POST")) {

            final String requestBody = httpRequest.getRequestBody();

            Map<String, String> requestValues = new HashMap<>();
            final String[] firstSplit = requestBody.split("&");
            for (String s : firstSplit) {
                final String[] keyAndValue = s.split("=");
                requestValues.put(keyAndValue[0], keyAndValue[1]);
            }

            User user = new User(
                    requestValues.get("account"),
                    requestValues.get("password"),
                    requestValues.get("email")
            );

            InMemoryUserRepository.save(user);

            final HttpResponseBody httpResponseBody = new HttpResponseBody(
                    httpRequestLine.getRequestUri().getValue());

            final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                    httpRequestLine.getRequestUri().getValue(),
                    httpResponseBody.getValue()
            );

            return new HttpResponse(httpResponseBody, HttpStatus.SUCCESS, httpResponseHeader);
        }
        return null;
    }

    public HttpResponse loginProcess(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequestLine.getRequestUri().endWith("/login") &&
                httpRequestLine.getMethod().equals("GET")
        ) {

            if (httpRequest.hasSessionId()) {
                final String sessionId = httpRequest.getSessionId();
                final Optional<Session> session = sessionManager.findSession(sessionId);
                if (session.isPresent()) {
                    final HttpResponseBody httpResponseBody = new HttpResponseBody(
                            "/index.html");

                    final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                            "/index.html",
                            httpResponseBody.getValue()
                    );
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

        if (httpRequestLine.getRequestUri().endWith("login") &&
                httpRequestLine.getMethod().equals("POST")
        ) {
            final String requestBody = httpRequest.getRequestBody();

            Map<String, String> requestValues = new HashMap<>();
            final String[] firstSplit = requestBody.split("&");
            for (String s : firstSplit) {
                final String[] keyAndValue = s.split("=");
                requestValues.put(keyAndValue[0], keyAndValue[1]);
            }

            if (isExistUser(requestValues)) {

                final HttpResponseBody httpResponseBody = new HttpResponseBody(
                        "/index.html");

                final HttpResponseHeader httpResponseHeader = new HttpResponseHeader(
                        "/index.html",
                        httpResponseBody.getValue()
                );

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
        return null;
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
