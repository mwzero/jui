package com.jui.html.elements;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnorderedList extends WebElement {
	
	String label;
	List<UnorderedListItem> items;
	
    public UnorderedList() {
    	
    	super("UL");
    	items = new ArrayList<>();
    }
    
    public  UnorderedList add(String label, String icon, WebContainer link, WebContainer content, boolean active) {
    	
    	UnorderedListItem item = new UnorderedListItem(
			label, 
			icon, 
    		link != null ? link.clientId() : null, 
			content != null ? content.clientId() : null);
			
    	items.add(item);
    	
		return this;
    	
    }
    
}
