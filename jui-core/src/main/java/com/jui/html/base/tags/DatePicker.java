package com.jui.html.base.tags;

import com.jui.html.WebComponent;

import lombok.extern.java.Log;

@Log
public class DatePicker extends WebComponent {
	
    private String label;

	public DatePicker(String label) {
		
        this.label = label;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
		
	