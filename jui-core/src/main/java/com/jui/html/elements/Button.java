package com.jui.html.elements;

import com.jui.html.WebElement;

public class Button extends WebElement {
	
    String label;
    String type;
    String onClick;

    public Button(String label, String type, String onClick, Runnable onServerSide) {
    	
    	super("Button");
    	
        this.label = label;
        this.onClick = onClick;
        this.type = type;
        this.onServerSide = onServerSide;
    }
}
