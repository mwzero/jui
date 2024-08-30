package com.jui;

import java.io.IOException;

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
	JuiContainer main;
	JuiContainer sidebar;
	JuiContainer header;
	
	@Builder
	public JuiWebApplication() {
		
		log.info("Building new PageHandler");
		try {
			
			engine = new TemplateHelper(true, ".");
			
			template = "templates/jui";
			//template = "templates/simple-bootstrap";
			
			main = new JuiContainer(engine);
			sidebar = new JuiContainer(engine);
			header = new JuiContainer(engine);
			
			chart = main.chart; 
			text = main.text;
			input = main.input;
			
			
		} catch (IOException e) {
			log.error("Impossible to use TemplateEngine [{}]", e.getLocalizedMessage());
		}
	}
	
	public WebContext getContext() {
		
		return main.getContext();
		
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
	
	public void write(String... args) {this.main.write(args);}
	public void write ( Object obj ) { this.main.write(obj);}
	public void divider() { this.main.divider();}
	public void divider(String color) {this.main.getContext().add(new Divider(color));}
	public Text markdown(String... args) { return this.main.text.markdown(args);}

}
