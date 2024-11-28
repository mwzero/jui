package com.jui.html.elements;

import java.util.List;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class CheckBox extends WebElement {
	
    String label;
    List<String> values;

	public CheckBox(String label, List<String> values) {
		
		super("CheckBox");
		
        this.label = label;
        this.values = values;
	}

}
