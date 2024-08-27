package com.jui;

import java.io.IOException;
import java.lang.reflect.Field;

import com.jui.annotations.JUI;
import com.jui.html.Divider;
import com.jui.html.charts.ChartHandler;
import com.jui.html.input.InputHandler;
import com.jui.html.text.Text;
import com.jui.html.text.TextHandler;
import com.jui.http.SimpleHttpServer;
import com.jui.templates.TemplateHelper;
import com.jui.utils.Markdown;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class JuiPage {
	
	private TemplateHelper engine;
	private WebContext context;
	
	//handlers
	public ChartHandler chart; 
	public TextHandler text;
	public InputHandler input;
	
	String template;
	
	@Builder
	public JuiPage() {
		
		log.info("Building new PageHandler");
	
		try {
			engine = new TemplateHelper(true, "templates");
			
			context = new WebContext(engine);
			
			chart = new ChartHandler(context);
			text = new TextHandler(context);
			input = new InputHandler(context);
			
			template = "simple-bootstrap-1";
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	//TODO: questo potrebbe essere più complesso
	public void write(String... args) {
		 for (String arg : args) {
			 String text = Markdown.builder().build().render(arg);
			 this.context.add(new Text(text, false, true));
		 }
	}
	
	public void write ( Object obj ) {
		for ( Field field : obj.getClass().getDeclaredFields()) {
			
			if (  field.isAnnotationPresent(JUI.class) ) {
			
				// Recuperare il tipo del campo
	            Class<?> fieldType = field.getType();

				try {

		            if (fieldType == int.class) {
		            	
		            	JUI column = field.getAnnotation(JUI.class);
		            	this.input.slider("eccolo", column.min(), column.max(), 15);
		                
		            }
		                
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				
			}
			
		}
	}
	
	public void divider() {
		this.context.add(new Divider());
	}
	
	public void startJuiServer() {
		try {
			SimpleHttpServer.start(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}