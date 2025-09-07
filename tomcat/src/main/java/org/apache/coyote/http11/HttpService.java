package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class HttpService {

    public HttpResponse basicProcess(HttpRequest httpRequest) {
        if (!httpRequest.getRequestUri().isExistFile()) {
            return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.NOT_FOUND);
        }
        return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.SUCCESS);
    }

    public HttpResponse registerProcess(HttpRequest httpRequest) {
        if (httpRequest.getRequestUri().endWith("/register")) {
            httpRequest.modifyRequestUri(httpRequest.getRequestUri().convertHtmlFromUri());
            return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.SUCCESS);
        }
        // TODO : 회원가입을 logic 작성
        return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.SUCCESS);

    }

    public HttpResponse loginProcess(HttpRequest httpRequest) {
        if (httpRequest.getRequestUri().endWith("/login")) {
            httpRequest.modifyRequestUri(httpRequest.getRequestUri().convertHtmlFromUri());
            return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.SUCCESS);
        }
        Map<String, String> queryParams = new HashMap<>();
        String queryParam = httpRequest.getRequestUri().convertToQueryParams();
        String[] split = queryParam.split("[=&]");
        queryParams.put(split[0], split[1]);
        queryParams.put(split[2], split[3]);

        if (isExistUser(queryParams)) {
            httpRequest.modifyRequestUri(new RequestUri("/index.html"));
            return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.FOUND);
        }
        httpRequest.modifyRequestUri(new RequestUri("/401.html"));
        return new HttpResponse(httpRequest.calculateBytes(), HttpStatus.UNAUTHORIZED);
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
