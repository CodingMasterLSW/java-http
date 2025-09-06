package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String line = br.readLine();

            HttpRequest httpRequest = HttpRequest.createFrom(line);

            httpRequest.queryParameterProcess();

            byte[] bytes = httpRequest.calculateBytes();

            final String responseHeader = getResponseHeader(httpRequest.getRequestUri().getValue(), bytes);

            outputStream.write(responseHeader.getBytes());
            outputStream.write(bytes);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseHeader(final String requestUri, final byte[] bytes) {
        String responseHeader = "";
        if (requestUri.endsWith(".css")) {
            responseHeader = getHeader(bytes, "css");
        }
        if (requestUri.endsWith(".html") || requestUri.equals("/")) {
            responseHeader = getHeader(bytes, "html");
        }

        if (requestUri.endsWith(".js")) {
            responseHeader = getHeader(bytes, "js");
        }
        return responseHeader;
    }

    private String getHeader(final byte[] bytes, String type) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                ""
        ) + "\r\n";
    }
}
