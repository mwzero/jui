package com.jui.html.apis;

import java.util.List;

import com.jui.html.WebContext;
import com.jui.html.elements.CheckBox;
import com.jui.html.elements.ColorPicker;
import com.jui.html.elements.DatePicker;
import com.jui.html.elements.FileInput;
import com.jui.html.elements.Input;
import com.jui.html.elements.Radio;
import com.jui.html.elements.Select;
import com.jui.html.elements.Slider;

public class OtherElements extends BaseElements {

	public OtherElements(WebContext context) {
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
