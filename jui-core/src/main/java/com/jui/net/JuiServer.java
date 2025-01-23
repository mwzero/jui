package com.jui.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.jui.JuiAppSettings;
import com.jui.net.handlers.HandlerBase;
import com.jui.net.handlers.HandlerFile;
import com.jui.net.handlers.HandlerJuiRequest;
import com.jui.net.handlers.HandlerJuiWebSocket;
import com.jui.net.handlers.HandlerRequest;
import com.jui.net.handlers.IHandlerWebSocket;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import lombok.Getter;
import lombok.Setter;
import lombok.AccessLevel;

import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiServer {
	
	private static final String GOOGLE_REDIRECT_URI = "https://127.0.0.1:8443/google/callback";

	@Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
	HandlerBase juiHttpHandler;
	
	@Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
	IHandlerWebSocket juiWebSocketHandler;
	
	@Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
	JuiAppSettings page;
	
	String docRoot;
	boolean classLoading;
	String host;
	int port;
	
	boolean enableWebSocket;
	int wssPort;
	
	boolean enableHttps;
	boolean enableAuthentication;
	
	public JuiServer(JuiAppSettings page) {
		this.page = page;
	}

	public void start() {
		
		String rootDoc = "html/";
		Boolean classLoading = true;
		String host = "0.0.0.0";
		int port = 8080;
		enableWebSocket = false;
		enableHttps = false;
		
		if ( page.layout() != null)
			rootDoc = "html-" + page.layout() + "/";
		
		this.start(rootDoc, classLoading, host, port);
	}
	
	public void start(String docRoot, boolean classLoading, String host, int port) {
	
		this.juiHttpHandler = new HandlerJuiRequest();
		this.juiWebSocketHandler = new HandlerJuiWebSocket();
		this.classLoading = true;
		this.docRoot = docRoot;
		this.host = host;
		this.port = port;
		this.wssPort = 8025;
		
		this.createAndStart();
	}
	
	
	protected void createAndStart() {
		
		log.info("Starting embedded JUI Server. docRoot[%s]".formatted(docRoot));
		HttpServer server = null;
		try {

			if ( enableAuthentication | enableHttps ) {
				
				try {
		            String keystorePath = "app_keystore.jks";
		            String password = "securepassword";

		            StandardKeystoreGenerator generator = new StandardKeystoreGenerator(keystorePath, password);
		            KeyStore keystore = generator.createKeystore();

					// Configura SSL
			        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
			        kmf.init(keystore, password.toCharArray());
			        
					SSLContext sslContext = SSLContext.getInstance("TLS");
			        sslContext.init(kmf.getKeyManagers(), null, null);
			        
					server = HttpsServer.create(new InetSocketAddress(8443), 0);
					((HttpsServer)server).setHttpsConfigurator(new HttpsConfigurator(sslContext));
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			else {
				server = HttpServer.create(new InetSocketAddress(port), 0);
			}
			
			if ( enableAuthentication ) {
				
				final String GOOGLE_CLIENT_ID = System.getenv("GOOGLE_CLIENT_ID");
				final String GOOGLE_CLIENT_SECRET = System.getenv("GOOGLE_CLIENT_SECRET");

				
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
		                    "?client_id=" + GOOGLE_CLIENT_ID +
		                    "&redirect_uri=" + URLEncoder.encode(GOOGLE_REDIRECT_URI, "UTF-8") +
		                    "&response_type=code" +
		                    "&scope=openid%20profile%20email";
		            exchange.getResponseHeaders().set("Location", url);
		            exchange.sendResponseHeaders(302, -1);
		        });

		        // Google Callback
		        server.createContext("/google/callback", exchange -> {
		            Map<String, String> queryParams = parseQueryParams(exchange.getRequestURI().getQuery());
		            if (queryParams.containsKey("code")) {
		                String code = queryParams.get("code");
		                String token = exchangeCodeForToken(code, "https://oauth2.googleapis.com/token",
		                        GOOGLE_CLIENT_ID, GOOGLE_CLIENT_SECRET, GOOGLE_REDIRECT_URI);
		                String userInfo = fetchUserInfo("https://openidconnect.googleapis.com/v1/userinfo", token);

		                String response = "<h1>Google Login Success</h1><p>User Info: " + userInfo + "</p>";
		                exchange.sendResponseHeaders(200, response.getBytes().length);
		                try (OutputStream os = exchange.getResponseBody()) {
		                    os.write(response.getBytes());
		                }
		            } else {
		                exchange.sendResponseHeaders(400, 0);
		            }
		        });
			} else {
				server.createContext("/", new HandlerFile(docRoot));
			}
			server.createContext("/jui", juiHttpHandler);
			server.createContext("/css", new HandlerFile(docRoot));
	        server.createContext("/js", new HandlerFile(docRoot));
	        server.createContext("/send_get", new HandlerRequest());
	        server.createContext("/send_post", new HandlerRequest());
	        server.createContext("/favicon.ico", new HandlerFile(docRoot));

	        server.setExecutor(null); // creates a default executor
	        server.start();
	        
	        log.info("Server is running on port [%s]".formatted(port));
	        log.info("\r\n"
	        		+ "       _  _    _  _____ \r\n"
	        		+ "      | || |  | ||_   _|\r\n"
	        		+ "      | || |  | |  | |  \r\n"
	        		+ "  _   | || |  | |  | |  \r\n"
	        		+ " | |__| || |__| | _| |_ \r\n"
	        		+ "  \\____/  \\____/ |_____|\r\n"
	        		+ "                        \r\n"
	        		+ "                        ");
	        
	        if ( enableWebSocket ) {

	        	
		        JuiSimpleWebSocketServer wss = JuiSimpleWebSocketServer.create(new InetSocketAddress(wssPort), 8025);
				wss.createContext("/ws/jui", juiWebSocketHandler);
		        wss.start();    
		        log.info("WSS listening on port[%d]".formatted(wssPort));
	        }
	        
	        
	        
		} catch (IOException e) {
			
			log.severe("HttpServer not started. Error[%s]".formatted(e.getLocalizedMessage()));
		}
	}
	
	private static String exchangeCodeForToken(String code, String tokenUrl, String clientId, String clientSecret, String redirectUri) throws IOException {
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

    private static String fetchUserInfo(String userInfoUrl, String token) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(userInfoUrl).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + token);

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    private static Map<String, String> parseQueryParams(String query) {
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
