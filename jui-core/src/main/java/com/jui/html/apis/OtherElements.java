package com.jui.html.apis;

import java.util.ArrayList;
import java.util.List;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.html.WebElementContext;
import com.jui.html.elements.DropDownButton;

import com.jui.html.elements.Text;
import com.jui.html.elements.UnorderedList;
import com.jui.html.elements.UnorderedListItem;
import com.jui.processors.MarkdownProcessor;

public class OtherElements extends BaseElements {

	public OtherElements(WebElementContext context) {
		super(context);
	}

	public void write(String... args) {
		
		 for (String arg : args) {
			 String text = MarkdownProcessor.render(arg);
			 context.add(new Text(text, false, true));
		 }
	}
	
	public void write ( Object obj ) {
		JuiAnnotationHelper.write(context, obj);
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
			UnorderedListItem uiItem = new UnorderedListItem(item);
			uiList.add(uiItem);
		});
		ul.setItems(uiList);
		context.add(ul);
		return ul;
	}
	
}
