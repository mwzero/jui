package com.jui.html.elements;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Button extends WebElement {
	
    String label;
    String type;
    String onClick;
    
    WebContainer container;

    public Button(String label, String type) {
    	super("Button");
    }
    		
    public Button(String label, String type, String onClick, Runnable onServerSide) {
    	
    	super("Button");
    	
        this.label = label;
        this.onClick = onClick;
        this.type = type;
        this.onServerSide = onServerSide;

    }
}
