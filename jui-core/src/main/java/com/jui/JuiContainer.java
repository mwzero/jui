package com.jui;

import java.lang.reflect.Field;

import com.jui.annotations.JUI;
import com.jui.builders.ChartBuilder;
import com.jui.builders.InputBuilder;
import com.jui.html.Divider;
import com.jui.html.Table;
import com.jui.html.Text;
import com.jui.html.UnorderedList;
import com.jui.processors.MarkdownProcessor;
import com.jui.templates.TemplateHelper;
import com.st.JuiDataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class JuiContainer {
	
	private String cliendId;
	private WebContext context;
	
	//handlers
	public ChartBuilder chart; 
	public InputBuilder input;
	
	public JuiContainer(TemplateHelper engine, int counter) {
		
		log.debug("New JuiContainer[{}]", counter);
		cliendId = "div_" + counter;
	
		context = new WebContext(engine);
		
		//initialzing Builder
		chart = new ChartBuilder(context);
		input = new InputBuilder(context);
			
	}
	
	public void write(String... args) {
		 for (String arg : args) {
			 String text = MarkdownProcessor.builder().build().render(arg);
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
	
	public UnorderedList ul(String label) {
		UnorderedList ul = new UnorderedList();
		ul.setLabel(label);
		this.context.add(ul);
		return ul;
		
	}
	
	public Table table(String caption, JuiDataFrame df) {
		
		Table table = new Table();
		table.setDf(df);
		table.setCaption(caption);
		
		this.context.add(table);
		return table;
	}
	
	public void divider(String color) {this.context.add(new Divider(color));}
	
	public Text markdown(String... args) { return this.input.markdown(args);}
	
}