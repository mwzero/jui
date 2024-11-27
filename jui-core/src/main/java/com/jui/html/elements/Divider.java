package com.jui.html.elements;

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
    	super("Divider");
    }
    
    public Divider(String color) {
    	this();
    	this.color = color;
    }
}
