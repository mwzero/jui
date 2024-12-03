package com.jui.html.apis;

import java.util.ArrayList;
import java.util.List;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.html.WebElementContext;
import com.jui.html.elements.CheckBox;
import com.jui.html.elements.ColorPicker;
import com.jui.html.elements.DatePicker;
import com.jui.html.elements.Divider;
import com.jui.html.elements.DropDownButton;
import com.jui.html.elements.FileInput;
import com.jui.html.elements.Input;
import com.jui.html.elements.Radio;
import com.jui.html.elements.Select;
import com.jui.html.elements.Slider;
import com.jui.html.elements.Table;
import com.jui.html.elements.Text;
import com.jui.html.elements.UnorderedList;
import com.jui.html.elements.UnorderedListItem;
import com.jui.processors.MarkdownProcessor;
import com.st.DataFrame;

public class OtherElements extends BaseElements {

	public OtherElements(WebElementContext context) {
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
	
	public void write(String... args) {
		 for (String arg : args) {
			 String text = MarkdownProcessor.builder().build().render(arg);
			 context.add(new Text(text, false, true));
		 }
	}
	
	public void write ( Object obj ) {
		JuiAnnotationHelper.write(context, obj);
	}
	
	public Divider divider() {
		
		Divider divider = new Divider();
		context.add(divider);
		return divider;
		
	}
	
	public UnorderedList ul(String label) {
		UnorderedList ul = new UnorderedList();
		ul.setLabel(label);
		context.add(ul);
		return ul;
		
	}
	
	public DropDownButton dropDownButton(String label, List<String> list) {
		DropDownButton ul = new DropDownButton();
		ul.setLabel(label);
		
		List<UnorderedListItem> uiList = new ArrayList<>(); 
		list.stream().forEach(item -> {
			UnorderedListItem uiItem = new UnorderedListItem(item, "", "", "");
			uiList.add(uiItem);
		});
		ul.setItems(uiList);
		context.add(ul);
		return ul;
	}
	
	public Table table(String caption, DataFrame df) {
		
		Table table = new Table();
		table.setDf(df);
		table.setCaption(caption);
		context.add(table);
		return table;
	}

	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.setDf(df.limit(limit));
		table.setDf(df);
		table.setCaption(caption);
		
		context.add(table);
		return table;
	}
	

}
