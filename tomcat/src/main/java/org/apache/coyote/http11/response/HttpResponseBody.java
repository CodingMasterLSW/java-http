package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class HttpResponseBody {

    private final byte[] value;

    public HttpResponseBody(final String target) {
        this.value = calculateBytes(target);
    }

    public byte[] calculateBytes(String taget) {
        if (taget.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        URL resource = getClass().getClassLoader().getResource("static" + taget);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
        }

        try {
            return Files.readAllBytes(Path.of(resource.getFile()));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽지 못 했습니다.");
        }
    }

    public byte[] getValue() {
        return value;
    }
}
