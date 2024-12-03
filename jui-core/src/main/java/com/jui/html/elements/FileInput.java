package com.jui.html.elements;

import com.jui.html.WebElement;

public class FileInput extends WebElement {
	
    String label;

	public FileInput(String label) {
		super("FileInput");
		
        this.label = label;
	}
}