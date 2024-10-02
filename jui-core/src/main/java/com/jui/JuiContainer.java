package com.jui;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.jui.annotations.JUI;
import com.jui.builders.ChartBuilder;
import com.jui.builders.InputBuilder;
import com.jui.html.Divider;
import com.jui.html.DropDownButton;
import com.jui.html.Table;
import com.jui.html.Text;
import com.jui.html.UnorderedList;
import com.jui.html.UnorderedListItem;
import com.jui.processors.MarkdownProcessor;
import com.jui.templates.TemplateHelper;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public class JuiContainer implements AutoCloseable {
	
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
	
	public DropDownButton dropDownButton(String label, List<String> list) {
		DropDownButton ul = new DropDownButton();
		ul.setLabel(label);
		
		List<UnorderedListItem> uiList = new ArrayList<>(); 
		list.stream().forEach(item -> {
			UnorderedListItem uiItem = new UnorderedListItem(item, "", "", "");
			uiList.add(uiItem);
		});
		ul.setItems(uiList);
		this.context.add(ul);
		return ul;
	}
	
	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.setDf(df.limit(limit));
		table.setCaption(caption);
		
		this.context.add(table);
		return table;
	}
	
	public void divider(String color) {this.context.add(new Divider(color));}
	
	public Text markdown(String... args) { return this.input.markdown(args);}

	public void title(String text) {
		
		this.context.add(new Text(text, false, true));
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}