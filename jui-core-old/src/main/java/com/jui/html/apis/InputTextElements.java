package com.jui.html.apis;

import com.jui.html.WebElementContext;
import com.jui.html.elements.Input;

public class InputTextElements extends BaseElements {
	
	public InputTextElements(WebElementContext context) {
		
		super(context);
		
	}
	
	public Input input(String text, String value, String placeholder) { 
		Input input = new Input(text, true, true, value, placeholder);
		context.add(input);
		return input;
	}
	
	public Input input() {

		Input input = new Input();
		context.add(input);
		return input;
	}
}
