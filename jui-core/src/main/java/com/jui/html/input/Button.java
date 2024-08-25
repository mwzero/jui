package com.jui.html.input;

import com.jui.html.WebComponent;

public class Button extends WebComponent {
	
    private final String label;
    private final String type;
    private final String onClick;

    public Button(String label, String type, String onClick) {
        this.label = label;
        this.onClick = onClick;
        this.type = type;
    }

	@Override
	public String render() {
		return "<button onclick=\"" + onClick + "; return false;\" class=\"btn btn-" + type + "\">" + label + "</button>";
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
