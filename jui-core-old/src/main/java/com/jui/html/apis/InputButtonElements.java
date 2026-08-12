package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Button;
import com.jui.html.elements.FormButton;
import com.jui.html.elements.FormButton.ButtonType;

public class InputButtonElements extends BaseElements {
	
	public InputButtonElements(WebElementContext context) {
		
		super(context);
		
	}
	
	public FormButton formButton(String label, ButtonType type, String onClick) { return (FormButton) this.context.add(new FormButton(label, type, onClick));}
	public FormButton submitbutton(String label, String onClick) { 
		return (FormButton) this.context.add(new FormButton(label, ButtonType.Primary, onClick));}
	
	public Button button(String label, String type, String onClick) { 
		
		return (Button) this.context.add(new Button(label, type, onClick));
	}


	public Button button(String label, String type) { 
		
		return (Button) this.context.add(new Button(label, type));
	}
}
