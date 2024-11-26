package com.jui.html.elements;

import com.jui.html.WebComponent;

public class TextArea extends WebComponent {
	
    String text;
    boolean readonly = true;
    int rows = 3;

	public TextArea(String text, int rows, boolean readonly) {
		
        this.text = text;
        this.readonly = readonly;
        this.rows = rows;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
