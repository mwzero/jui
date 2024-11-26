package com.jui.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.html.apis.ChartElements;
import com.jui.html.apis.ContainerElements;
import com.jui.html.apis.ContainerElements.ContainerType;
import com.jui.html.elements.Divider;
import com.jui.html.elements.DropDownButton;
import com.jui.html.elements.Table;
import com.jui.html.elements.Text;
import com.jui.html.elements.UnorderedList;
import com.jui.html.elements.UnorderedListItem;
import com.jui.processors.MarkdownProcessor;
import com.jui.html.apis.InputButtonElements;
import com.jui.html.apis.InputSelectionElements;
import com.jui.html.apis.OtherElements;
import com.jui.html.apis.TextElements;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiContainer extends WebComponent implements AutoCloseable {
	
	ContainerType type;
	
	/* API delegates */
	@Delegate
    private final TextElements textApis;
	
	@Delegate
    private final ContainerElements containerApis;
	
	@Delegate 
	private final ChartElements chartElements;
	
	@Delegate 
	private final InputButtonElements inputButtonElements;
	
	@Delegate 
	private final InputSelectionElements inputSelectionElements;
	
	@Delegate 
	private final OtherElements otherElements;
	
	
	public JuiContainer(String key) {  this(key, ContainerType.DIV, null); }
	public JuiContainer(String key, ContainerType type) { this(key, type, null);}
	public JuiContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		super();
		log.fine("New JuiContainer:" + key);
		
		this.clientId = key;
		this.type = type;
		if ( attributes != null ) this.attributes = attributes;
		
		//Apis Delegates
		textApis = new TextElements(this.getWebContext());
		containerApis = new ContainerElements(this.getWebContext());
		chartElements = new ChartElements(this.getWebContext());
		inputButtonElements = new InputButtonElements(this.getWebContext());
		inputSelectionElements = new InputSelectionElements(this.getWebContext());
		otherElements = new OtherElements(this.getWebContext());
		
	}
	
	@Override
	public String getHtml() {
		
		if ( type == ContainerType.COL ) {
			
			int width = (int) this.attributes.get(ContainerElements.WIDTH_ATTRIBUTES);
			return """
					<div id="%s" class="col-%s" >{{content}}</div>
				   """.formatted(this.getClientId(), width);
		} else {
			
			return """
					<div class="row" id="%s">{{content}}</div>
				   """.formatted(clientId);
			
		}
		
	}
	
	public void write(String... args) {
		 for (String arg : args) {
			 String text = MarkdownProcessor.builder().build().render(arg);
			 this.getWebContext().add(new Text(text, false, true));
		 }
	}
	
	public void write ( Object obj ) {
		JuiAnnotationHelper.write(getWebContext(), obj);
	}
	
	public Divider divider() {
		
		Divider divider = new Divider();
		this.getWebContext().add(divider);
		return divider;
		
	}
	
	public UnorderedList ul(String label) {
		UnorderedList ul = new UnorderedList();
		ul.setLabel(label);
		this.getWebContext().add(ul);
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
		this.getWebContext().add(ul);
		return ul;
	}
	
	public Table table(String caption, DataFrame df) {
		
		Table table = new Table();
		table.setDf(df);
		table.setCaption(caption);
		this.getWebContext().add(table);
		return table;
	}

	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.setDf(df.limit(limit));
		table.setDf(df);
		table.setCaption(caption);
		
		this.getWebContext().add(table);
		return table;
	}
	

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public JuiContainer columns(String key) {
		
		return getComponents().stream()
		        .filter(component -> component instanceof JuiContainer && ((JuiContainer) component).type == ContainerType.ROW)
		        .map(component -> (JuiContainer) component)
		        .flatMap(row -> row.getComponents().stream())
		        .filter(component2 -> component2 instanceof JuiContainer && ((JuiContainer) component2).type == ContainerType.COL)
		        .map(component2 -> (JuiContainer) component2)
		        .filter(col -> col.getClientId().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}
	
	public JuiContainer tabs(String key) {
		
		return getComponents().stream()
		        .filter(component -> component instanceof JuiContainer && ((JuiContainer) component).type == ContainerType.TABS)
		        .map(component -> (JuiContainer) component)
		        .flatMap(row -> row.getComponents().stream())
		        .filter(component2 -> component2 instanceof JuiContainer && ((JuiContainer) component2).type == ContainerType.TAB)
		        .map(component2 -> (JuiContainer) component2)
		        .filter(col -> col.getClientId().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}
	
	public Collection<WebComponent> getComponents() {
		
		return getWebContext().getLinkedMapContext().values();
	}

}