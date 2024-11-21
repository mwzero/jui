package com.jui.html.apis;

import com.jui.html.WebContext;
import com.jui.html.tags.Button;
import com.jui.html.tags.FormButton;
import com.jui.html.tags.FormButton.ButtonType;

public class InputButtonElements extends BaseElements {
	
	public InputButtonElements(WebContext context) {
		
		super(context);
		
	}
	
	public FormButton formButton(String label, ButtonType type, String onClick) { return (FormButton) this.context.add(new FormButton(label, type, onClick));}
	public FormButton submitbutton(String label, String onClick) { 
		return (FormButton) this.context.add(new FormButton(label, ButtonType.Primary, onClick));}
	
	public Button button(String label, String type, String onClick, Runnable onServerSide) { return (Button) this.context.add(new Button(label, type, onClick, onServerSide));}


}
