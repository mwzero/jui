package it.jui.framework.input;

import java.util.Base64;

public record UploadedFile(String name, String contentType, long size, String base64) {

    public byte[] bytes() {
        if (base64 == null || base64.isBlank()) return new byte[0];
        return Base64.getDecoder().decode(base64);
    }
}
