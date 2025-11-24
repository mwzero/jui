
package it.example.streamlitlike.server;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UploadHandler implements HttpHandler {
    private final SessionManager sessionManager;

    public UploadHandler(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String sessionId = CookieUtil.ensureSessionId(exchange);
        Map<String, String> state = sessionManager.getSession(sessionId);

        Headers headers = exchange.getRequestHeaders();
        String contentType = headers.getFirst("Content-Type");
        if (contentType == null || !contentType.startsWith("multipart/form-data")) {
            sendText(exchange, 400, "Invalid Content-Type");
            return;
        }

        // Estrai boundary
        String boundary = null;
        for (String part : contentType.split(";")) {
            part = part.trim();
            if (part.startsWith("boundary=")) {
                boundary = part.substring("boundary=".length());
                break;
            }
        }
        if (boundary == null) {
            sendText(exchange, 400, "Missing boundary");
            return;
        }

        byte[] body = readAllBytes(exchange.getRequestBody());
        Map<String, Part> parts = parseMultipart(body, boundary);

        Part filePart = parts.get("file");
        Part componentPart = parts.get("component");

        String componentId = componentPart != null ? componentPart.value : "unknown";
        String filename = filePart != null ? filePart.filename : "";
        byte[] fileBytes = filePart != null ? filePart.data : new byte[0];

        // Aggiorna stato (per demo salviamo solo nome e size, NON i bytes)
        state.put(componentId + "Name", filename);
        state.put(componentId + "Size", String.valueOf(fileBytes.length));
        sessionManager.updateSession(sessionId, Map.of(componentId + "Name", filename, componentId + "Size", String.valueOf(fileBytes.length)));

        String msg = "<p style='color:#0b7a0b;'>Uploaded " + escape(filename) + " (" + fileBytes.length + " bytes)</p>";
        sendHtml(exchange, 200, msg);
    }

    private static class Part {
        String name;
        String filename;
        String value;
        byte[] data;
    }

    private Map<String, Part> parseMultipart(byte[] body, String boundary) {
        Map<String, Part> map = new HashMap<>();
        byte[] delimiter = ("--" + boundary).getBytes(StandardCharsets.UTF_8);
        byte[] endDelimiter = ("--" + boundary + "--").getBytes(StandardCharsets.UTF_8);

        int idx = 0;
        while (idx < body.length) {
            int start = indexOf(body, delimiter, idx);
            if (start < 0) break;
            start += delimiter.length;

            // skip \r\n
            if (start + 2 < body.length && body[start] == '\r' && body[start + 1] == '\n') start += 2;

            int headerEnd = indexOf(body, "\r\n\r\n".getBytes(StandardCharsets.UTF_8), start);
            if (headerEnd < 0) break;

            String headers = new String(body, start, headerEnd - start, StandardCharsets.UTF_8);
            int contentStart = headerEnd + 4;

            int next = indexOf(body, delimiter, contentStart);
            int end = next >= 0 ? next - 2 : indexOf(body, endDelimiter, contentStart) - 2; // -2 per \r\n
            if (end < 0) break;

            byte[] data = Arrays.copyOfRange(body, contentStart, end);

            Part part = new Part();

            for (String hline : headers.split("\r\n")) {
                hline = hline.trim();
                if (hline.toLowerCase().startsWith("content-disposition")) {
                    // es: form-data; name="file"; filename="image.png"
                    for (String token : hline.split(";")) {
                        token = token.trim();
                        if (token.startsWith("name=")) {
                            part.name = unquote(token.substring(5));
                        } else if (token.startsWith("filename=")) {
                            part.filename = unquote(token.substring(9));
                        }
                    }
                }
                // content-type si potrebbe leggere se necessario
            }

            if (part.filename != null) {
                part.data = data;
            } else {
                part.value = new String(data, StandardCharsets.UTF_8);
            }
            map.put(part.name, part);
            idx = next >= 0 ? next : body.length;
        }
        return map;
    }

    private int indexOf(byte[] haystack, byte[] needle, int from) {
        outer: for (int i = from; i <= haystack.length - needle.length; i++) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack[i + j] != needle[j]) continue outer;
            }
            return i;
        }
        return -1;
    }

    private byte[] readAllBytes(InputStream is) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        is.transferTo(bos);
        return bos.toByteArray();
    }

    private void sendText(HttpExchange ex, int status, String text) throws IOException {
        byte[] b = text.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
        ex.sendResponseHeaders(status, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private void sendHtml(HttpExchange ex, int status, String html) throws IOException {
        byte[] b = html.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "text/html; charset=utf-8");
        ex.sendResponseHeaders(status, b.length);
        try (OutputStream os = ex.getResponseBody()) { os.write(b); }
    }

    private String unquote(String s) {
        if (s.startsWith("\"") && s.endsWith("\"")) return s.substring(1, s.length() - 1);
        return s;
    }

    private String escape(String s) {
        return s == null ? "" : s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
