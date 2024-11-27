package com.jui.html.elements;

import com.jui.html.WebComponent;

public class FileInput extends WebComponent {
	
    String label;

	public FileInput(String label) {
		super("FileInput");
		
        this.label = label;
	}
}