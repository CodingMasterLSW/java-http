package org.apache.coyote.http11.service;

import static org.reflections.Reflections.log;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class LoginService {

    public Optional<User> login(String requestBody) {
        Map<String, String> requestValues = new HashMap<>();
        final String[] firstSplit = requestBody.split("&");
        for (String s : firstSplit) {
            final String[] keyAndValue = s.split("=");
            requestValues.put(keyAndValue[0], keyAndValue[1]);
        }

        return isExistUser(requestValues);
    }

    private Optional<User> isExistUser(final Map<String, String> queryParams) {
        try {
            final User user = InMemoryUserRepository.findByAccount(queryParams.get("account"))
                    .orElseThrow(() -> new IllegalArgumentException("이런 유저는 없답니다."));
            if (user.checkPassword(queryParams.get("password"))) {
                log.info(user.toString());
                return Optional.of(user);
            }
        } catch (IllegalArgumentException e) {
            log.info(e.getMessage());
            return Optional.empty();
        }
        return Optional.empty();
    }
}
