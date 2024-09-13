package com.jui.html.input;

import com.jui.html.WebComponent;

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
	public String render() {
		
		String js = onClick;
		
		if ( this.onServerSide != null ) {
			js= "%s;sendClick('%s');return false;".formatted(this.onClick, this.getKey());
		}
		
		return "<button onclick=\"" + js + "\" class=\"btn btn-" + type + "\">" + label + "</button>";
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
