package com.jui.html.tags;

import java.util.List;

import com.jui.html.WebComponent;

import lombok.extern.java.Log;

@Log
public class CheckBox extends WebComponent {
	
    String label;
    List<String> values;

	public CheckBox(String label, List<String> values) {
		
        this.label = label;
        this.values = values;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
		
	
}
