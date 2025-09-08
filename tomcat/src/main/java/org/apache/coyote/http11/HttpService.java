package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class HttpService {

    public HttpResponse basicProcess(HttpRequestLine httpRequestLine) {
        if (!httpRequestLine.getRequestUri().isExistFile()) {
            return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.NOT_FOUND);
        }
        return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.SUCCESS);
    }

    public HttpResponse registerProcess(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequestLine.getRequestUri().endWith("/register") &&
                httpRequestLine.getMethod().equals("GET")) {
            httpRequestLine.modifyRequestUri(httpRequestLine.getRequestUri().convertHtmlFromUri());
            return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.SUCCESS);
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
            httpRequestLine.modifyRequestUri(new RequestUri("/index.html"));
            return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.SUCCESS);
        }
        return null;
    }

    public HttpResponse loginProcess(HttpRequest httpRequest) {
        final HttpRequestLine httpRequestLine = httpRequest.getHttpRequestLine();
        if (httpRequestLine.getRequestUri().endWith("/login") &&
                httpRequestLine.getMethod().equals("GET")) {
            httpRequestLine.modifyRequestUri(httpRequestLine.getRequestUri().convertHtmlFromUri());
            return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.SUCCESS);
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
                httpRequestLine.modifyRequestUri(new RequestUri("/index.html"));
                return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.FOUND);
            }
            httpRequestLine.modifyRequestUri(new RequestUri("/401.html"));
            return new HttpResponse(httpRequestLine.calculateBytes(), HttpStatus.UNAUTHORIZED);

        }
        return null;
    }

    private boolean isExistUser(final Map<String, String> queryParams) {
        try {
            final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("이런 유저는 없답니다."));
            if (user.checkPassword(queryParams.get("password"))) {
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
