package com.jui.html.input;

import java.util.List;

import com.jui.WebContext;
import com.jui.html.input.FormButton.ButtonType;

public class InputHandler {
	
	WebContext context;
	
	public InputHandler(WebContext context) {
		this.context = context;
	}

	public Input input(String text, String value, String placeholder) { 
		Input input = new Input(text, true, true, value, placeholder);
		context.add(input);
		return input;
	}
	
	public Input input(InputBuilder builder) {

		Input input = new Input();
		context.add(input);
		
		if ( builder.c_label != null ) {
			input.setLabel(builder.c_label.getValue());
			//context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(builder.c_lat.getKey()));
			context.addRelations(input.getKey(), "document.getElementById('%s').value=value".formatted(builder.c_label.getKey()));
		} else 
			input.setLabel(builder.label);
		
		
		if ( builder.c_value != null ) {
			input.setValue(builder.c_value.getValue());
			//context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(builder.c_lat.getKey()));
			context.addRelations(builder.c_value.getKey(), "document.getElementById('%s').value=value".formatted(input.getKey()));
		} else 
			input.setValue(builder.value);
		
		input.setInput(builder.input);
		input.setReadonly(builder.readonly);
		return input;
	}
	
	public FormButton formButton(String label, ButtonType type, String onClick) { return (FormButton) this.context.add(new FormButton(label, type, onClick));}
	public FormButton submitbutton(String label, String onClick) { 
		return (FormButton) this.context.add(new FormButton(label, ButtonType.Primary, onClick));}
	
	public Button button(String label, String type, String onClick, Runnable onServerSide) { return (Button) this.context.add(new Button(label, type, onClick, onServerSide));}
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
