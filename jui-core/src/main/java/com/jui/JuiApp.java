package com.jui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.grizzly.PortRange;
import org.glassfish.grizzly.http.server.CLStaticHttpHandler;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.grizzly.http.server.NetworkListener;
import org.glassfish.grizzly.http.server.StaticHttpHandler;
import org.glassfish.tyrus.server.Server;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.jui.html.Button;
import com.jui.html.Divider;
import com.jui.html.InputHandler;
import com.jui.html.Table;
import com.jui.html.Text;
import com.jui.html.WebComponent;
import com.jui.html.charts.ChartHandler;
import com.jui.templates.TemplateHelper;
import com.jui.utils.Utils;
import com.st.JuiDataFrame;

import jakarta.websocket.DeploymentException;
import jakarta.websocket.Session;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JuiApp {
	
	public static JuiApp jui = JuiApp.getInstance();
	
	TemplateHelper engine;
	
	@Setter
	String template;
	
	// Web Application containers
	public List<JuiContainer> main;
	public JuiContainer sidebar;
	int iContainer = 0;
	
	// only to work over main container as default
	public ChartHandler chart;
	public InputHandler input;
	
	private static synchronized JuiApp getInstance() {
		
		if (jui == null) {
			jui = new JuiApp();
		}
		return jui;
	}

	protected JuiApp() {

		log.info("Building new PageHandler");
		try {

			engine = new TemplateHelper(true, ".");

			// template = "templates/jui";
			template = "templates/bootstrap-simple";

			main = new ArrayList<>();
			main.add(new JuiContainer(engine, ++iContainer));

			sidebar = new JuiContainer(engine, ++iContainer);

			chart = main.get(0).chart;
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
		return this.main.get(0).markdown(args);
	}

	public Table table(String caption, JuiDataFrame df, String... args) {
		return this.main.get(0).table(caption, df);
	}

	public String render() {
		
		StringBuilder html = new StringBuilder();

		for (WebComponent component : this.main.get(0).getContext().getLinkedMapContext().values()) {
			
			html.append(component.render());
			
		}
		
		if ( getMain().size() == 1) {
			try {
				html.append("""
						<script>
							elementMapping=%s;
							elementPostData=%s;
						</script>
						""".formatted(
								Utils.buildJsonString(getMain().get(0).getContext().relations, "source", "commands"),
								Utils.buildJsonString(getMain().get(0).getContext().elementPostData, "source", "commands")
								));
			} catch (JsonProcessingException e) {
				
				log.error("Error Processing relations for components. [{}]", e.getLocalizedMessage());
			}
		}
		
		return html.toString();
			
	}

	
	public void start() {
		
		this.start("html/", true, "0.0.0.0", 8080, 8025);
	}
	
	public void start(String docRoot, boolean classLoading, String host, int port, int wsPort) {
		
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
        
		//httpserver.getServerConfiguration().addHttpHandler(new RequestHandler(), "/js");
		try {

			webServer.start();
			System.out.println("--- httpserver is running");

			Server server;
			server = new Server(host, wsPort, "/ws", null, WebSocketEndpoint.class);
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

	public WebComponent executeServerAction(String id) {
		
		WebComponent  component = this.main.get(0).getContext().getLinkedMapContext().get(id);
		if ( component != null )
			component.executeServerAction();
		return component;
		
	}

}
