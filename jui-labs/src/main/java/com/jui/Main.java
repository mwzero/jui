package com.jui;

import java.io.IOException;

import javax.servlet.http.HttpServlet;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import jakarta.websocket.DeploymentException;

/**
 * An Example Embedded Apache Tomcat with an anonymous inner class
 * {@link HttpServlet}.
 *
 * @author John Yeary <jyeary@bluelotussoftware.com>
 * @version 1.0.0
 */
public class Main {

	public static void main(String[] args) throws DeploymentException, IOException {

		// Definisci l'interfaccia utente
        App.button("Cliccami", () -> {
            System.out.println("Bottone cliccato!");
            // Potresti voler aggiornare l'interfaccia utente qui
        });
        
		HttpServer httpserver = HttpServer.createSimpleServer("src/main/resources/html", 8080);
		httpserver.getServerConfiguration().addHttpHandler(new FileHandler(), "/js");
		httpserver.start();
		
		Server server;
		server = new Server("localhost", 8025, "/ws", null, MyWebSocketEndpoint.class);
		try {
			server.start();
			System.out.println("--- server is running");
			
			System.out.println("Press any key to stop the server...");
			System.in.read();
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			server.stop();
		}
		
		
		
		
        
		System.out.println("Press any key to stop the server...");
		System.in.read();

	}
}
