package com.jui;

import java.io.IOException;

import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.tyrus.server.Server;

import com.jui.html.WebComponent;

import jakarta.websocket.DeploymentException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JuiAppGrizzly extends JuiApp {
	
	public static JuiAppGrizzly jui = JuiAppGrizzly.getInstance();
	
	private static synchronized JuiAppGrizzly getInstance() {
		
		if (jui == null) {
			jui = new JuiAppGrizzly();
		}
		return jui;
	}

	protected JuiAppGrizzly() {

		super();
	}
	
	@Override
	public void start(String docRoot, boolean classLoading, String host, int port) {
		
		log.info("Starting http/ws Grizzly Server");
		
		HttpServer webServer;
		webServer = HttpServer.createSimpleServer();
        webServer.getServerConfiguration().setName("JUI");
        
        if ( classLoading ) {
        	webServer.getServerConfiguration().addHttpHandler(new CLStaticHttpHandler(JuiApp.class.getClassLoader(), docRoot), "/");
        } else {
        	webServer.getServerConfiguration().addHttpHandler(new StaticHttpHandler(docRoot), "/");
        }
        final NetworkListener listener = new NetworkListener("grizzly", host, new PortRange(port));
        webServer.addListener(listener);
        
        webServer.getServerConfiguration().addHttpHandler(new JuiRequestHandler(), "/jui");
		try {

			webServer.start();
			System.out.println("--- httpserver is running");

			Server server;
			server = new Server(host, 8025, "/ws", null, WebSocketEndpoint.class);
			server.start();
			System.out.println("--- websocket server is running");

			System.out.println("Press any key to stop the server...");
			System.in.read();
			webServer.shutdownNow();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

	public WebComponent executeServerAction(String id) {
		
		WebComponent  component = this.main.get(0).getContext().getLinkedMapContext().get(id);
		if ( component != null )
			component.executeServerAction();
		return component;
		
	}

}
