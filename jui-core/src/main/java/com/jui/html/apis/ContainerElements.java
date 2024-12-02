package com.jui.html.apis;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;
import com.jui.html.WebAttributes;
import com.jui.html.WebElementContext;
import com.jui.html.apis.ContainerElements.ContainerType;

public class ContainerElements extends BaseElements {
	
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
	
	public ContainerElements(WebElementContext context) {
		super(context);
		
	}
	
	//Layouts and Containers
	protected WebContainer addContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		WebContainer container = new WebContainer(key,type, attributes);
		context.add(container);
		
		return container;	
	}
	
	public WebContainer container(String key) { return this.addContainer(key, ContainerType.DIV, null);}
	public WebContainer dialog(String key, String Title) { return this.addContainer(key, ContainerType.DIALOG, null);}
	public WebContainer expander(String key, String Title) { return this.addContainer(key, ContainerType.EXPANDER, null);}
	public WebContainer popover(String key, String Title) { return this.addContainer(key, ContainerType.POPOVER, null);}
	public WebContainer empty(String key) { return this.addContainer(key, ContainerType.POPOVER, null);}
	
	public List<WebContainer> tabs(List<String> of) {
		
		List<WebContainer> tabs = new ArrayList<WebContainer>();
		
		WebContainer row = new WebContainer("", ContainerType.TABS);
		boolean isActive = true;
		
		for (String tab : of) {
			
			WebContainer col = new WebContainer(tab, ContainerType.TAB);
			col.addAttribute(WebAttributes.ACTIVE_ATTRIBUTES, isActive);
			row.add( col);
			tabs.add(col);
			
			isActive = false;
			
		}
		this.context.add(row);
		
		return tabs;
	}

	public List<WebContainer> columns(Map<String, Integer> of) {
		
		List<WebContainer> cols = new ArrayList<WebContainer>();
		
		WebContainer row = new WebContainer("", ContainerType.ROW, null);
		
		for (Entry<String, Integer> column : of.entrySet()) {
			
			WebContainer col = new WebContainer(column.getKey(), ContainerType.COL, Map.of( WebAttributes.WIDTH_ATTRIBUTES, column.getValue()));
			cols.add(col);
			row.add( col);
			
		}
		
		this.context.add(row);
		
		return cols;
	}
	
	public WebContainer columns(String key) {
		
		return getComponents().stream()
		        .filter(component -> component instanceof WebContainer && ((WebContainer) component).type() == ContainerType.ROW)
		        .map(component -> (WebContainer) component)
		        .flatMap(row -> row.getComponents().stream())
		        .filter(component2 -> component2 instanceof WebContainer && ((WebContainer) component2).type() == ContainerType.COL)
		        .map(component2 -> (WebContainer) component2)
		        .filter(col -> col.key().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}
	
	public WebContainer tabs(String key) {
		
		return getComponents().stream()
		        .filter(component -> component instanceof WebContainer && ((WebContainer) component).type() == ContainerType.TABS)
		        .map(component -> (WebContainer) component)
		        .flatMap(row -> row.getComponents().stream())
		        .filter(component2 -> component2 instanceof WebContainer && ((WebContainer) component2).type() == ContainerType.TAB)
		        .map(component2 -> (WebContainer) component2)
		        .filter(col -> col.key().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}
	
	public Collection<WebElement> getComponents() {
		
		return context.getLinkedMapContext().values();
	}
}
