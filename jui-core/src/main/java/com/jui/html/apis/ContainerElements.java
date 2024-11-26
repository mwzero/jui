package com.jui.html.apis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jui.html.JuiContainer;
import com.jui.html.WebContext;

public class ContainerElements extends BaseElements {
	
	public final static String WIDTH_ATTRIBUTES = "width";
	
	public enum ContainerType {
		DIV,
		ROW,
		COL,
		DIALOG,
		EXPANDER,
		POPOVER,
		TABS,
		TAB,
		EMPTY
		
	};
	
	public ContainerElements(WebContext context) {
		super(context);
		
	}
	
	//Layouts and Containers
	protected JuiContainer addContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		JuiContainer container = new JuiContainer(key, ContainerType.DIV, null);
		context.add(container);
		
		return container;	
	}
	
	public JuiContainer container(String key) { return this.addContainer(key, ContainerType.DIV, null);}
	public JuiContainer dialog(String key, String Title) { return this.addContainer(key, ContainerType.DIALOG, null);}
	public JuiContainer expander(String key, String Title) { return this.addContainer(key, ContainerType.EXPANDER, null);}
	public JuiContainer popover(String key, String Title) { return this.addContainer(key, ContainerType.POPOVER, null);}
	public JuiContainer empty(String key) { return this.addContainer(key, ContainerType.POPOVER, null);}
	
	public List<JuiContainer> tabs(Map<String, Integer> of) {
		
		List<JuiContainer> cols = new ArrayList<JuiContainer>();
		
		JuiContainer row = new JuiContainer("", ContainerType.TABS, null);
		
		for (Entry<String, Integer> column : of.entrySet()) {
			
			JuiContainer col = new JuiContainer(column.getKey(), ContainerType.TAB, Map.of( WIDTH_ATTRIBUTES, column.getValue()));
			row.getWebContext().add( col);
			cols.add(col);
			
		}
		this.context.add(row);
		
		return cols;
	}

	public List<JuiContainer> columns(Map<String, Integer> of) {
		
		List<JuiContainer> cols = new ArrayList<JuiContainer>();
		
		JuiContainer row = new JuiContainer("", ContainerType.ROW, null);
		
		for (Entry<String, Integer> column : of.entrySet()) {
			
			JuiContainer col = new JuiContainer(column.getKey(), ContainerType.COL, Map.of( WIDTH_ATTRIBUTES, column.getValue()));
			cols.add(col);
			row.getWebContext().add( col);
			
		}
		
		this.context.add(row);
		
		return cols;
	}
}
