package com.jui.html;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Button extends WebComponent {
	
    private final String label;
    private final String type;
    private final String onClick;

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
