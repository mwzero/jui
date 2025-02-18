package com.jui.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyStore;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;

import com.jui.JuiAppSettings;
import com.jui.modules.JuiModule;
import com.jui.net.handlers.HandlerBase;
import com.jui.net.handlers.HandlerFile;
import com.jui.net.handlers.HandlerJuiRequest;
import com.jui.net.handlers.HandlerJuiWebSocket;
import com.jui.net.handlers.HandlerRequest;
import com.jui.net.handlers.IHandlerWebSocket;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpsConfigurator;
import com.sun.net.httpserver.HttpsServer;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiServer {
	
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
	JuiModule enableAuthentication;
	
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
		
		log.fine("Starting embedded JUI Server. docRoot[%s]".formatted(docRoot));
		
		boolean httpsEnabled=false;
		HttpServer server = null;
		try {

			if ( (enableAuthentication != null) | enableHttps ) {
				httpsEnabled = true;
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
			
			if ( enableAuthentication != null ) {
				enableAuthentication.createContexts(server);
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
	        
	        log.info("\r\n"
	        		+ " *************************\r\n"
	        		+ "       _  _    _  _____ \r\n"
	        		+ "      | || |  | ||_   _|\r\n"
	        		+ "      | || |  | |  | |  \r\n"
	        		+ "  _   | || |  | |  | |  \r\n"
	        		+ " | |__| || |__| | _| |_ \r\n"
	        		+ "  \\____/  \\____/ |_____|\r\n"
	        		+ "                        \r\n"
	        		+ " *************************");
	        
	        if ( httpsEnabled ) {
	        	log.info("Server is running on port [%s]".formatted(8443));
	        } else {
	        	log.info("Server is running on port [%s]".formatted(port));
	        }
	        
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
}
