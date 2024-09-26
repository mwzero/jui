package com.jui;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpServer;

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
			html.append("""
					<script>
						elementMapping=%s;
						elementPostData=%s;
					</script>
					""".formatted(
							Utils.buildJsonString(getMain().get(0).getContext().relations, "source", "commands"),
							Utils.buildJsonString(getMain().get(0).getContext().elementPostData, "source", "commands")
							));
		}
		
		return html.toString();
			
	}

	
	public void start() {
		
		this.start("html/", true, "0.0.0.0", 8080);
	}
	
	public void start(String docRoot, boolean classLoading, String host, int port) {
		
		HttpServer server;
		try {
			server = HttpServer.create(new InetSocketAddress(port), 0);
			server.createContext("/jui", new JuiRequestHandler());
	        
			server.createContext("/css", new FileHandler());
	        server.createContext("/js", new FileHandler());
	        server.createContext("/html", new FileHandler());
	        server.createContext("/send_get", new RequestHandler());
	        server.createContext("/send_post", new RequestHandler());
	        
	        
	        server.createContext("/favicon.ico", new FileHandler());

	        server.setExecutor(null); // creates a default executor
	        server.start();
	        
	        log.info("Server is running on port [{}]", port);
	        
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
