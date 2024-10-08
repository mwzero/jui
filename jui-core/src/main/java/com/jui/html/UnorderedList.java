package com.jui.html;

import java.util.ArrayList;
import java.util.List;

import com.jui.JuiContainer;

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
    			link != null ? link.getCliendId() : null, content != null ? content.getCliendId() : null);
    	items.add(item);
    	
		return this;
    	
    }
    
}
