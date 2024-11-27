package com.jui.html.elements;

import com.jui.html.WebComponent;

public class TextArea extends WebComponent {
	
    String text;
    boolean readonly = true;
    int rows = 3;

	public TextArea(String text, int rows, boolean readonly) {
		
		super("TextArea");
		
        this.text = text;
        this.readonly = readonly;
        this.rows = rows;
	}
}
