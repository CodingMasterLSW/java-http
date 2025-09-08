package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseHeader;
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
            String firstLine = br.readLine();

            final HttpRequestHeader httpRequestHeader = parseHeaders(br);
            HttpRequestLine httpRequestLine = HttpRequestLine.createFrom(firstLine);
            HttpRequest httpRequest = new HttpRequest(httpRequestLine, httpRequestHeader, br);

            if (httpRequestLine.getRequestUri().hasUri("/login")) {
                HttpResponse httpResponse = httpService.loginProcess(httpRequest);
                final HttpResponseHeader responseHeader = httpResponse.getResponseHeader();

                outputStream.write((httpResponse.getResponseLine() + " \r\n").getBytes());
                outputStream.write(responseHeader.parseAndGetAllHeader().getBytes());
                outputStream.write(httpResponse.getBodyValue());
                outputStream.flush();
                return;
            }

            if (httpRequestLine.getRequestUri().hasUri("/register")) {
                HttpResponse httpResponse = httpService.registerProcess(httpRequest);
                final HttpResponseHeader responseHeader = httpResponse.getResponseHeader();
                outputStream.write((httpResponse.getResponseLine() + " \r\n").getBytes());
                outputStream.write(responseHeader.parseAndGetAllHeader().getBytes());
                outputStream.write(httpResponse.getBodyValue());
                return;
            }

            HttpResponse httpResponse = httpService.basicProcess(httpRequestLine);
            final HttpResponseHeader responseHeader = httpResponse.getResponseHeader();

            outputStream.write((httpResponse.getResponseLine() + " \r\n").getBytes());
            outputStream.write(responseHeader.parseAndGetAllHeader().getBytes());
            outputStream.write(httpResponse.getBodyValue());
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


    private HttpRequestHeader parseHeaders(final BufferedReader br) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = br.readLine()) != null && !headerLine.isEmpty()) {
            int idx = headerLine.indexOf(":");
            String key = headerLine.substring(0, idx).trim();
            String value = headerLine.substring(idx + 1).trim();
            headers.put(key, value);
        }
        return new HttpRequestHeader(headers);
    }
}
