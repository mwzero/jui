package com.jui.html.elements;

import com.jui.html.WebElement;

public class FormButton extends WebElement {
	
    final String label;
    final ButtonType type;
    final String onClick;
    
    public enum ButtonType {
    	Primary,
    	Secondary,
    	Success,
    	Danger,
    	Warning,
    	Info,
    	Light,
    	Dark,
    	Link
    }

    public FormButton(String label, ButtonType type, String onClick) {
    	
    	super("FormButton");
    	
        this.label = label;
        this.onClick = onClick;
        this.type = type;
    }

	@Override
	public String getHtml() {
		
		return """
				<button class="btn btn-%s">%s</button>
			   """.formatted(type.name().toLowerCase(), label);
	}
}
