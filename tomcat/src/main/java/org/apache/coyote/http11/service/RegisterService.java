package org.apache.coyote.http11.service;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.util.HashMap;
import java.util.Map;

public class RegisterService {

    public void saveRegister(String requestBody) {
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
    }
}
