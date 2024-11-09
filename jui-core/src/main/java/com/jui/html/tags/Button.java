package com.jui.html.tags;

import com.jui.html.WebComponent;

import lombok.extern.slf4j.Slf4j;

public class Button extends WebComponent {
	
    String label;
    String type;
    String onClick;

    public Button(String label, String type, String onClick, Runnable onServerSide) {
        this.label = label;
        this.onClick = onClick;
        this.type = type;
        this.onServerSide = onServerSide;
    }

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
