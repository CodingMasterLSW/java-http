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
    private final HttpService httpService = new HttpService();

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

            if (httpRequest.getRequestUri().hasUri("/login")) {
                HttpResponse httpResponse = httpService.loginProcess(httpRequest);
                String responseHeader = getResponseHeader(httpRequest.getRequestUri().getValue(), httpResponse);
                outputStream.write(responseHeader.getBytes());
                outputStream.write(httpResponse.getBody());
                outputStream.flush();
                return;
            }
            HttpResponse httpResponse = httpService.basicProcess(httpRequest);
            String responseHeader = getResponseHeader(httpRequest.getRequestUri().getValue(), httpResponse);
            outputStream.write(responseHeader.getBytes());
            outputStream.write(httpResponse.getBody());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseHeader(final String requestUri, HttpResponse httpResponse) {
        String responseHeader = "";
        if (requestUri.endsWith(".css")) {
            responseHeader = getHeader(httpResponse, "css");
        }
        if (requestUri.endsWith(".html") || requestUri.equals("/")) {
            responseHeader = getHeader(httpResponse, "html");
        }

        if (requestUri.endsWith(".js")) {
            responseHeader = getHeader(httpResponse, "js");
        }
        return responseHeader;
    }

    private String getHeader(HttpResponse httpResponse, String type) {
        return String.join("\r\n",
                "HTTP/1.1 " + httpResponse.getHttpStatus().getCode() + " " + httpResponse.getHttpStatus().getMessage(),
                "Content-Type: text/" + type + ";charset=utf-8 ",
                "Content-Length: " + httpResponse.getBody().length + " ",
                ""
        ) + "\r\n";
    }
}
