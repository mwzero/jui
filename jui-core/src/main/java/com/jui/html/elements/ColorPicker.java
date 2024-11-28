package com.jui.html.elements;

import com.jui.html.WebElement;

public class ColorPicker extends WebElement {
	
    String label;

	public ColorPicker(String label) {
		super("ColorPicker");
		
        this.label = label;
	}
}