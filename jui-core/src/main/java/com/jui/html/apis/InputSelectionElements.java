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

public class InputSelectionElements extends BaseElements {

	public InputSelectionElements(WebContext context) {
		super(context);
	}

	public Slider slider(String text, int min, int max, int value) {
		
		Slider slider = new Slider(text, min, max, value);
		this.context.add(slider);
		return slider;
	}

	public Radio radio(String label, List<String> values ) { return (Radio) this.context.add(new Radio(label, values));}
	public CheckBox checkbox(String label, List<String> values ) { return (CheckBox) this.context.add(new CheckBox(label, values));}
	
	//select
	public Select select(String label, List<String> values ) { return (Select) this.context.add(new Select(label, values));}
	//public Select select(String label, SimpleTable st) { return (Select) this.context.add(new Select(label, st));}
	
	public FileInput file_uploader(String label) { return (FileInput) this.context.add(new FileInput(label));}
	public ColorPicker color_picker(String label) { return (ColorPicker) this.context.add(new ColorPicker(label));}
	public DatePicker date_input(String label) { return (DatePicker) this.context.add(new DatePicker(label));}

}
