package com.jui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jui.html.Divider;
import com.jui.html.charts.ChartHandler;
import com.jui.html.input.InputHandler;
import com.jui.html.text.Text;
import com.jui.html.text.TextHandler;
import com.jui.http.SimpleHttpServer;
import com.jui.templates.TemplateHelper;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public class JuiWebApplication {
	
	//template Engine
	TemplateHelper engine;
	String template;
	
	//Web Application containers
	public List<JuiContainer> main;
	public JuiContainer sidebar;
	public JuiContainer header;
	int iContainer=0;
	
	
	@Builder
	public JuiWebApplication() {
		
		log.info("Building new PageHandler");
		try {
			
			engine = new TemplateHelper(true, ".");
			
			template = "templates/jui";
			//template = "templates/simple-bootstrap";
			
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
	
	public void start() {
		try {
			SimpleHttpServer.start(this);
			
		} catch (IOException e) {
			log.error("Impossible to start HTTP Server [{}]", e.getLocalizedMessage());
		}
	}
	
	//only to work over main container as default
	public ChartHandler chart; 
	public TextHandler text;
	public InputHandler input;
	
	public void write(String... args) {this.main.get(0).write(args);}
	public void write ( Object obj ) { this.main.get(0).write(obj);}
	public void divider() { this.main.get(0).divider();}
	public void divider(String color) {this.main.get(0).getContext().add(new Divider(color));}
	public Text markdown(String... args) { return this.main.get(0).text.markdown(args);}

}
