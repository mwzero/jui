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
	List<ListItem> items;
	
    public UnorderedList() {
    	this.setId("UL");
    	items = new ArrayList<>();
    }
    
    public  UnorderedList add(String label, String icon, JuiContainer link, boolean active) {
    	
    	ListItem item = new ListItem(label, icon, 
    			link != null ? "switchNavBar('%s')".formatted(link.getCliendId()) :"#");
    	items.add(item);
    	
		return this;
    	
    }
    
}
