package org.apache.coyote.http11.service;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.coyote.http11.Cookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;

public class LoginService {

    private final SessionManager sessionManager = SessionManager.getInstance();

    public boolean login(String requestBody) {
        Map<String, String> requestValues = new HashMap<>();
        final String[] firstSplit = requestBody.split("&");
        for (String s : firstSplit) {
            final String[] keyAndValue = s.split("=");
            requestValues.put(keyAndValue[0], keyAndValue[1]);
        }

        if (isExistUser(requestValues)) {
            return true;
        }
        return false;

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
