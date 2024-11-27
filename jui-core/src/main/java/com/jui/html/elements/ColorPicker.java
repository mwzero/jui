package com.jui.html.elements;

import com.jui.html.WebComponent;

public class ColorPicker extends WebComponent {
	
    String label;

	public ColorPicker(String label) {
		super("ColorPicker");
		
        this.label = label;
	}
}