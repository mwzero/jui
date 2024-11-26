package com.jui.html.elements;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.JuiContainer;
import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UnorderedList extends WebComponent {
	
	String label;
	List<UnorderedListItem> items;
	
    public UnorderedList() {
    	
    	this.setId("UL");
    	items = new ArrayList<>();
    }
    
    public  UnorderedList add(String label, String icon, JuiContainer link, JuiContainer content, boolean active) {
    	
    	UnorderedListItem item = new UnorderedListItem(label, icon, 
    			link != null ? link.getClientId() : null, content != null ? content.getClientId() : null);
    	items.add(item);
    	
		return this;
    	
    }
    
}
