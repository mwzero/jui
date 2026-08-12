package com.jui.modules.google;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.jui.JuiApp;
import com.jui.JuiSession;
import com.jui.modules.Authenticator;
import com.jui.modules.OAuth2Authentication;
import com.sun.net.httpserver.HttpServer;

import lombok.extern.java.Log;

@Log
public class GoogleAuthenticator extends Authenticator {
	
	OAuth2Authentication oauth2;
	
	public GoogleAuthenticator(OAuth2Authentication oauth2) {
		
		log.fine("Initializing GoogleAuthenticator");
		this.oauth2 = oauth2;
	}
	
	@Override
	public void createContexts(HttpServer server) {
			
		// Homepage
        server.createContext("/", exchange -> {
            String response = """
                    <h1>OAuth2 Example</h1>
                    <a href="/google/login">Login with Google</a><br>
                    <a href="/github/login">Login with GitHub</a>
                    """;
            exchange.sendResponseHeaders(200, response.getBytes().length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(response.getBytes());
            }
        });
        
     // Google Login
        server.createContext("/google/login", exchange -> {
            String url = "https://accounts.google.com/o/oauth2/v2/auth" +
                    "?client_id=" + oauth2.clientId() +
                    "&redirect_uri=" + URLEncoder.encode(oauth2.callbackUrl(), "UTF-8") +
                    "&response_type=code" +
                    "&scope=openid%20profile%20email";
            exchange.getResponseHeaders().set("Location", url);
            exchange.sendResponseHeaders(302, -1);
        });

        // Google Callback
        server.createContext(oauth2.callbackUrlContext(), exchange -> {
            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
            if (queryParams.containsKey("code")) {
                String code = queryParams.get("code");
                String token = exchangeCodeForToken(code, "https://oauth2.googleapis.com/token",
                		oauth2.clientId(), oauth2.clientSecret(), oauth2.callbackUrl());
                String userInfo = fetchUserInfo("https://openidconnect.googleapis.com/v1/userinfo", token);
                
                //JuiSession session = new JuiSession(userInfo);

                Gson gson = new Gson();
                GoogleSession user = gson.fromJson(userInfo, GoogleSession.class);
                
                JuiSession session = 
                		JuiSession.builder().email(user.getEmail()).username(user.getName()).customSession(user).build();
                JuiApp.jui.session(session);
                
                String response = "<h1>Google Login Success</h1><p>User Info: " + userInfo + "</p>";
                exchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            } else {
                exchange.sendResponseHeaders(400, 0);
            }
        });
	} 
	
	protected static String exchangeCodeForToken(String code, String tokenUrl, String clientId, String clientSecret, String redirectUri) throws IOException {
        String payload = "code=" + code +
                "&client_id=" + clientId +
                "&client_secret=" + clientSecret +
                "&redirect_uri=" + URLEncoder.encode(redirectUri, "UTF-8") +
                "&grant_type=authorization_code";

        HttpURLConnection connection = (HttpURLConnection) new URL(tokenUrl).openConnection();
        connection.setDoOutput(true);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        try (OutputStream os = connection.getOutputStream()) {
            os.write(payload.getBytes());
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines()
                    .collect(Collectors.joining("\n"))
                    .replace("{", "")
                    .replace("}", "")
                    .replace("\"", "")
                    .split("access_token:")[1]
                    .split(",")[0];
        }
    }

	protected static String fetchUserInfo(String userInfoUrl, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(userInfoUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

	protected static Map<String, String> parseQueryParams(String query) {
        Map<String, String> queryParams = new HashMap<>();
        if (query != null) {
            Arrays.stream(query.split("&"))
                    .forEach(pair -> {
                        String[] keyValue = pair.split("=");
                        queryParams.put(keyValue[0], keyValue.length > 1 ? keyValue[1] : "");
                    });
        }
        return queryParams;
    }

}
