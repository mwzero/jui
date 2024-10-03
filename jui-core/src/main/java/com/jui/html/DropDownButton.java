package com.jui.html;

import java.util.ArrayList;
import java.util.List;

import com.jui.JuiContainer;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropDownButton extends WebComponent {
	
	String label;
	List<UnorderedListItem> items;
	
    public DropDownButton() {
    	
    	this.setId("DropDownButton");
    	items = new ArrayList<>();
    }
    
    public  DropDownButton add(String label, String icon, JuiContainer link, JuiContainer content, boolean active) {
    	
    	UnorderedListItem item = new UnorderedListItem(label, icon, 
    			link != null ? link.getCliendId() : null, content != null ? content.getCliendId() : null);
    	items.add(item);
    	
		return this;
    	
    }
    
}
