package com.jui.html.elements;

import java.util.ArrayList;
import java.util.List;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DropDownButton extends WebElement {
	
	String label;
	List<UnorderedListItem> items;
	
    public DropDownButton() {
    	
    	super("DropDownButton");
    	items = new ArrayList<>();
    }
    
    public  DropDownButton add(String label, String icon, String link, String content, WebContainer contentContainer, boolean active) {
    	
    	UnorderedListItem item = new UnorderedListItem(label);
		item.setIcon(icon);
		item.setLink(link);
		item.setContent(content);
		if ( contentContainer != null ) {

			item.setContainerId(contentContainer.clientId());
			contentContainer.renderOnLoad(false);
		}
    	items.add(item);
    	
		return this;
    	
    }
    
}
