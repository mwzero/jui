package com.jui.html;

import java.util.ArrayList;
import java.util.List;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.helpers.MarkdownProcessor;
import com.jui.helpers.TemplateHelper;
import com.jui.html.base.builders.InputBuilder;
import com.jui.html.base.tags.Divider;
import com.jui.html.base.tags.DropDownButton;
import com.jui.html.base.tags.Table;
import com.jui.html.base.tags.Text;
import com.jui.html.base.tags.UnorderedList;
import com.jui.html.base.tags.UnorderedListItem;
import com.jui.html.charts.builders.ChartBuilder;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public class JuiContainer implements AutoCloseable {
	
	private String cliendId;
	private WebContext context;
	
	//handlers
	public ChartBuilder chart; 
	public InputBuilder input;
	
	public JuiContainer(TemplateHelper engine, int counter) {
		
		log.fine("New JuiContainer[%d]".formatted(counter));
		cliendId = "div_" + counter;
	
		context = new WebContext(engine);
		
		//initialzing Builder
		chart = new ChartBuilder(context);
		input = new InputBuilder(context);
			
	}
	
	public void write(String... args) {
		 for (String arg : args) {
			 String text = MarkdownProcessor.builder().build().render(arg);
			 this.context.add(new Text(text, false, true));
		 }
	}
	
	public void write ( Object obj ) {
		JuiAnnotationHelper.write(context, obj);
	}
	
	public void divider() {
		this.context.add(new Divider());
	}
	
	public UnorderedList ul(String label) {
		UnorderedList ul = new UnorderedList();
		ul.setLabel(label);
		this.context.add(ul);
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
		this.context.add(ul);
		return ul;
	}
	
	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		if ( limit != 0)
			table.setDf(df.limit(limit));
		else
			table.setDf(df);
		table.setCaption(caption);
		
		this.context.add(table);
		return table;
	}
	
	public void divider(String color) {this.context.add(new Divider(color));}
	
	public Text markdown(String... args) { return this.input.markdown(args);}

	public void title(String text) {
		
		this.context.add(new Text(text, false, true));
		
	}

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}