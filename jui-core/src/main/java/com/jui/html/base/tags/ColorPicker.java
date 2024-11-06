package com.jui.html.base.tags;

import com.jui.html.WebComponent;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class ColorPicker extends WebComponent {
	
    String label;


	public ColorPicker(String label) {
		
        this.label = label;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}