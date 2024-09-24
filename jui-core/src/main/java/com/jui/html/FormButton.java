package com.jui.html;

import com.jui.html.WebComponent;

public class FormButton extends WebComponent {
	
    private final String label;
    private final ButtonType type;
    private final String onClick;
    
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
        this.label = label;
        this.onClick = onClick;
        this.type = type;
    }

	@Override
	public String render() {
		
		return """
				<button class="btn btn-%s">%s</button>
			   """.formatted(type.name().toLowerCase(), label);
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
