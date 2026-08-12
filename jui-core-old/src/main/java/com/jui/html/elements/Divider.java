package com.jui.html.elements;

import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Divider extends WebElement {
	
	String color;
	
    public Divider() {
    	super("Divider");
    }
    
    public Divider(String color) {
    	this();
    	this.color = color;
    }
}
