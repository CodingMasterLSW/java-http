package org.apache.coyote.http11.request;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class RequestUri {

    private final String value;

    public RequestUri(final String value) {
        this.value = value;
    }

    public RequestUri convertHtmlFromUri() {
        return new RequestUri(this.value + ".html");
    }

    public String convertToQueryParams() {
        return this.value.substring(this.value.indexOf("?") + 1);
    }

    public boolean hasUri(String compareValue) {
        return this.value.contains(compareValue);
    }

    public boolean endWith(String value) {
        return this.value.endsWith(value);
    }

    public boolean isExistFile() {
        return getClass().getClassLoader().getResource("static" + this.value) != null;
    }

    public byte[] calculateBytes() {
        if (this.value.equals("/")) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }
        URL resource = getClass().getClassLoader().getResource("static" + this.value);
        if (resource == null) {
            resource = getClass().getClassLoader().getResource("static/404.html");
        }

        try {
            return Files.readAllBytes(Path.of(resource.getFile()));
        } catch (IOException e) {
            throw new IllegalArgumentException("파일을 읽지 못 했습니다.");
        }
    }

    public String getValue() {
        return this.value;
    }
}
