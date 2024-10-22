package com.jui.playground.ctrls;

import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/proxy")
public class ReverseProxyController {

    private final RestTemplate restTemplate = new RestTemplate();

    @GetMapping("/**")
    public ResponseEntity<String> proxyGetRequest(HttpServletRequest request, @RequestHeader HttpHeaders headers) {
        String path = request.getRequestURI().replace("/proxy", ""); // Ottieni il percorso dinamico
        String targetUrl = "http://127.0.0.1:8080" + path; // Costruisci l'URL del server di destinazione

        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<String> response = restTemplate.exchange(targetUrl, HttpMethod.GET, requestEntity, String.class);

        // Modifica il contenuto HTML per riscrivere gli URL
        String modifiedBody = response.getBody()
            .replace("src=\"/", "src=\"/proxy/")
            .replace("href=\"/", "href=\"/proxy/");

        return new ResponseEntity<>(modifiedBody, response.getStatusCode());
    }
}
