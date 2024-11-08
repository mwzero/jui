package com.jui.html.base.tags;

import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Divider extends WebComponent {
	
	String color;
	
    public Divider() {
    	this.setId("Divider");
    }
    
    public Divider(String color) {
    	super();
    	this.color = color;
    }
}
