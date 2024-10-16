package com.jui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.jui.JuiAppAttributes.JuiAppAttributesBuilder;
import com.jui.builders.ChartBuilder;
import com.jui.builders.InputBuilder;

import com.jui.html.Button;
import com.jui.html.Divider;
import com.jui.html.Table;
import com.jui.html.Text;
import com.jui.html.WebComponent;
import com.jui.model.JuiContent;
import com.jui.net.JuiServer;
import com.jui.net.http.JuiRequestHandler;
import com.jui.net.http.JuiWebSocketHandler;
import com.jui.templates.TemplateHelper;
import com.jui.utils.Utils;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JuiApp {
	
	public static final JuiApp jui = new JuiApp();
	
	//used onServerAction response
	@Setter
	String juiResponse;
	
	
	TemplateHelper engine;
	
	// Web Application containers
	public List<JuiContainer> main;
	public JuiContainer sidebar;
	int iContainer = 0;
	
	// only to work over main container as default
	public ChartBuilder chart;
	public InputBuilder input;
	
	//Attributes
	JuiAppAttributesBuilder attrsBuilder;
	
	protected JuiApp() {

		log.info("JUI App: Start Initialization");
		try {

			engine = new TemplateHelper(true, ".");

			main = new ArrayList<>();
			main.add(new JuiContainer(engine, ++iContainer));

			sidebar = new JuiContainer(engine, ++iContainer);

			chart = main.get(0).chart;
			input = main.get(0).input;

		} catch (IOException e) {
			log.error("Impossible to use TemplateEngine [{}]", e.getLocalizedMessage());
		}
	}
	
	public JuiContainer addContainer() {

		JuiContainer container = new JuiContainer(this.engine, ++iContainer);
		main.add(container);
		return container;
	}

	public Button button(String label, String type, String onClick, Runnable onServerSide) { 
		return (Button) this.input.button(label, type, onClick, onServerSide);}

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

	public Table table(String caption, DataFrame df) {
		return this.main.get(0).table(caption, df, 0);
	}
	
	public Table table(String caption, DataFrame df, int limit) {
		return this.main.get(0).table(caption, df, limit);
	}

	public JuiContent render() {
		
		/**/
		JuiContent content = new JuiContent();
		
		
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
		
		content.setMain(html.toString());
		
		
		if ( this.sidebar.getContext().getLinkedMapContext() != null) {
			StringBuilder sidebar = new StringBuilder();
			for (WebComponent component : this.sidebar.getContext().getLinkedMapContext().values()) {
				sidebar.append(component.render());
			}
			content.setSidebar(sidebar.toString());
		} else
			content.setSidebar("");
		
		return content;
	}

	
	public void start() {
		
		String rootDoc = "html/";
		Boolean classLoading = true;
		String host = "0.0.0.0";
		int port = 8080;
		
		if ( attrsBuilder != null ) {
			JuiAppAttributes attrs = attrsBuilder.build();
			if ( attrs.getRootDoc() != null)
				rootDoc = "html-" + attrs.getRootDoc() + "/";
		}
		
		this.start(rootDoc, classLoading, host, port);
	}
	
	protected void start(String docRoot, boolean classLoading, String host, int port) {
	
		JuiServer.builder()
			.juiHttpHandler(new JuiRequestHandler())
			.juiWebSocketHandler(new JuiWebSocketHandler())
			.classLoading(true)
			.docRoot(docRoot)
			.host(host)
			.port(port)
			.wssPort(8025)
		.build()
		.start();
	}

	public WebComponent executeServerAction(String id) throws Exception{
		
		WebComponent  component = this.main.get(0).getContext().getLinkedMapContext().get(id);
		if ( component != null )
			component.executeServerAction();
		
		return component;
		
	}

	public JuiAppAttributesBuilder set_page_config() {
		
		
		attrsBuilder =  JuiAppAttributes.builder();
		return attrsBuilder;
		
		
		// TODO Auto-generated method stub
		
	}

	public JuiContainer[] columns(Map<String, Integer> of) {
		return null;
	}

	public JuiContainer columns(String string) {
		return null;
	}


}
