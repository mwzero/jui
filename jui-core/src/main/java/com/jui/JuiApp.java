package com.jui;

import java.io.IOException;
import java.util.*;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.tyrus.server.Server;

import com.jui.html.Divider;
import com.jui.html.Table;
import com.jui.html.WebComponent;
import com.jui.html.charts.ChartHandler;
import com.jui.html.input.Button;
import com.jui.html.input.InputHandler;
import com.jui.html.text.Text;
import com.jui.html.text.TextHandler;
import com.jui.templates.TemplateHelper;
import com.st.JuiDataFrame;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JuiApp {
	
	public static JuiApp jui = JuiApp.getInstance();

	public static JuiApp getInstance() {
		if (jui == null) {
			jui = new JuiApp();
		}
		return jui;
	}

	public WebComponent getWebComponentById(String id) {

		return this.main.get(0).getContext().getLinkedMapContext().get(id);
	}

	// template Engine
	TemplateHelper engine;

	@Setter
	String template;

	// Web Application containers
	public List<JuiContainer> main;
	public JuiContainer sidebar;
	public JuiContainer header;
	int iContainer = 0;

	@Builder
	public JuiApp() {

		log.info("Building new PageHandler");
		try {

			engine = new TemplateHelper(true, ".");

			// template = "templates/jui";
			template = "templates/bootstrap-simple";

			main = new ArrayList<>();
			main.add(new JuiContainer(engine, ++iContainer));

			sidebar = new JuiContainer(engine, ++iContainer);
			header = new JuiContainer(engine, ++iContainer);

			chart = main.get(0).chart;
			text = main.get(0).text;
			input = main.get(0).input;

		} catch (IOException e) {
			log.error("Impossible to use TemplateEngine [{}]", e.getLocalizedMessage());
		}
	}

	public JuiContainer getPage() {

		JuiContainer container = new JuiContainer(this.engine, ++iContainer);
		main.add(container);
		return container;
	}

	// only to work over main container as default
	public ChartHandler chart;
	public TextHandler text;
	public InputHandler input;
	
	public Button button(String label, String type, String onClick, Runnable onServerSide) { 
		return (Button) JuiApp.getInstance().input.button(label, type, onClick, onServerSide);}

	public void write(String... args) {
		this.main.get(0).write(args);
	}

	public void write(Object obj) {
		this.main.get(0).write(obj);
	}

	public void divider() {
		this.main.get(0).divider();
	}

	public void divider(String color) {
		this.main.get(0).getContext().add(new Divider(color));
	}

	public Text markdown(String... args) {
		return this.main.get(0).text.markdown(args);
	}

	public Table table(String caption, JuiDataFrame df, String... args) {
		return this.main.get(0).table(caption, df);
	}

	public void render(Session session) {

		StringBuilder html = new StringBuilder();

		for (WebComponent component : this.main.get(0).getContext().getLinkedMapContext().values()) {
			html.append(component.render());
		}

		// Invia l'HTML al client tramite WebSocket
		try {
			session.getAsyncRemote().sendText(html.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {

		HttpServer httpserver = HttpServer.createSimpleServer("src/main/resources/html", 8080);
		// httpserver.getServerConfiguration().addHttpHandler(new FileHandler(), "/js");
		try {

			httpserver.start();
			System.out.println("--- httpserver is running");

			Server server;
			server = new Server("localhost", 8025, "/ws", null, WebSocketEndpoint.class);
			server.start();
			System.out.println("--- websocket server is running");

			System.out.println("Press any key to stop the server...");
			System.in.read();

		} catch (IOException e) {
			e.printStackTrace();

		} catch (DeploymentException e) {
			e.printStackTrace();
		}
	}

}
