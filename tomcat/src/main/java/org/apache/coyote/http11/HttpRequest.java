package org.apache.coyote.http11;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {

    private final String method;
    private RequestUri requestUri;
    private final String version;

    public static HttpRequest createFrom(String line) {
        return new HttpRequest(line);
    }

    private HttpRequest(String line) {
        final String[] lines = line.split(" ");
        this.method = lines[0];
        this.requestUri = new RequestUri(lines[1]);
        this.version = lines[2];
    }

    private HttpRequest(final String method, final String requestUri, final String version) {
        this.method = method;
        this.requestUri = new RequestUri(requestUri);
        this.version = version;
    }

    public void queryParameterProcess() {
        if (!this.requestUri.hasUri("?")) {
            return;
        }
        if (this.requestUri.hasUri("/login")) {
            loginProcess();
        }
        this.requestUri =  requestUri.convertToHtmlUri();
    }

    private void loginProcess() {
        Map<String, String> queryParams = new HashMap<>();
        String queryParam = this.requestUri.convertToQueryParams();
        String[] split = queryParam.split("[=&]");
        queryParams.put(split[0], split[1]);
        queryParams.put(split[2], split[3]);
        loggingLoginUserInfo(queryParams);
    }

    private void loggingLoginUserInfo(final Map<String, String> queryParams) {
        try {
            final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("이런 유저는 없답니다."));
            if (user.checkPassword(queryParams.get("password"))) {
                log.info(user.toString());
            }
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
        }
    }

    public String getMethod() {
        return method;
    }

    public RequestUri getRequestUri() {
        return requestUri;
    }

    public String getVersion() {
        return version;
    }

    public byte[] calculateBytes() {
        return this.requestUri.calculateBytes();
    }
}
