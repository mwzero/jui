package com.jui.html.elements;

import com.jui.html.WebElement;

public class DatePicker extends WebElement {
	
    String label;

	public DatePicker(String label) {
		super("DatePicker");
		
        this.label = label;
	}
}
		
	