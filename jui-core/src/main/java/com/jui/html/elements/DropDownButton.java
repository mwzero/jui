package com.jui.html.elements;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.JuiContainer;
import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropDownButton extends WebComponent {
	
	String label;
	List<UnorderedListItem> items;
	
    public DropDownButton() {
    	
    	super("DropDownButton");
    	items = new ArrayList<>();
    }
    
    public  DropDownButton add(String label, String icon, JuiContainer link, JuiContainer content, boolean active) {
    	
    	UnorderedListItem item = new UnorderedListItem(label, icon, 
    			link != null ? link.clientId() : null, content != null ? content.clientId() : null);
    	items.add(item);
    	
		return this;
    	
    }
    
}
