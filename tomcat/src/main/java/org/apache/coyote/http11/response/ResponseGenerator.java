package org.apache.coyote.http11.response;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public class ResponseGenerator {

    public ResponseData calculateByteFromStaticResource(String target) {
        URL resource = getClass().getClassLoader().getResource("static" + target);
        try {
            if (resource == null) {
                resource = getClass().getClassLoader().getResource("static/404.html");
                final byte[] bytes = Files.readAllBytes(Path.of(resource.getFile()));
                return new ResponseData(bytes, false);
            }
            final byte[] bytes = Files.readAllBytes(Path.of(resource.getFile()));
            return new ResponseData(bytes, true);
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽지 못 했습니다.");
        }
    }
}
