package com.jui.html.apis;

import java.util.List;

import com.jui.html.WebContext;
import com.jui.html.tags.CheckBox;
import com.jui.html.tags.ColorPicker;
import com.jui.html.tags.DatePicker;
import com.jui.html.tags.FileInput;
import com.jui.html.tags.Input;
import com.jui.html.tags.Radio;
import com.jui.html.tags.Select;
import com.jui.html.tags.Slider;

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
