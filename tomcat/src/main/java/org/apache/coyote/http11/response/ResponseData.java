package org.apache.coyote.http11.response;

public record ResponseData(
        byte[] value,
        boolean isExistFile
) {

}
