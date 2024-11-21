package com.jui.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.helpers.MarkdownProcessor;
import com.jui.html.apis.ChartElements;
import com.jui.html.apis.ContainerElements;
import com.jui.html.apis.ContainerElements.ContainerType;
import com.jui.html.apis.InputButtonElements;
import com.jui.html.apis.InputSelectionElements;
import com.jui.html.apis.OtherElements;
import com.jui.html.apis.TextElements;
import com.jui.html.tags.Button;
import com.jui.html.tags.CheckBox;
import com.jui.html.tags.ColorPicker;
import com.jui.html.tags.DatePicker;
import com.jui.html.tags.Divider;
import com.jui.html.tags.DropDownButton;
import com.jui.html.tags.FileInput;
import com.jui.html.tags.FormButton;
import com.jui.html.tags.FormButton.ButtonType;
import com.jui.html.tags.Input;
import com.jui.html.tags.Radio;
import com.jui.html.tags.Select;
import com.jui.html.tags.Slider;
import com.jui.html.tags.Table;
import com.jui.html.tags.Text;
import com.jui.html.tags.UnorderedList;
import com.jui.html.tags.UnorderedListItem;
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
	
	String clientId;
	WebContext context;
	
	Map<String, Object> attributes;
	
	
	ContainerType type;
	
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
		
		log.fine("New JuiContainer:" + key);
		
		this.clientId = key;
		this.type = type;
		if ( attributes == null ) attributes = new HashMap<String, Object>(); 
		else this.attributes = attributes;
		
		context = new WebContext();
		
		//Apis Delegates
		textApis = new TextElements(context);
		containerApis = new ContainerElements(context);
		chartElements = new ChartElements(context);
		inputButtonElements = new InputButtonElements(context);
		inputSelectionElements = new InputSelectionElements(context);
		otherElements = new OtherElements(context);
		
	}
	
	@Override
	public String getHtml() {
		
		if ( type == ContainerType.COL ) {
			
			int width = (int) this.attributes.get(ContainerElements.WIDTH_ATTRIBUTES);
			return """
					<div id="%s" class="col-%s" >{{content}}</div>
				   """.formatted(this.clientId(), width);
		} else {
			
			return """
					<div class="row" id="%s">{{content}}</div>
				   """.formatted(clientId);
			
		}
		
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
	
	public Divider divider() {
		
		Divider divider = new Divider();
		this.context.add(divider );
		return divider;
		
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
	
	public Table table(String caption, DataFrame df) {
		
		Table table = new Table();
		table.setDf(df);
		table.setCaption(caption);
		this.context.add(table);
		return table;
	}

	public Table table(String caption, DataFrame df, int limit) {
		
		Table table = new Table();
		table.setDf(df.limit(limit));
		table.setDf(df);
		table.setCaption(caption);
		
		this.context.add(table);
		return table;
	}
	

	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}
	
	public JuiContainer columns(String key) {
		
		return this.context.getLinkedMapContext().values().stream()
		        .filter(component -> component instanceof JuiContainer && ((JuiContainer) component).type == ContainerType.ROW)
		        .map(component -> (JuiContainer) component)
		        .flatMap(row -> row.context().getLinkedMapContext().values().stream())
		        .filter(component2 -> component2 instanceof JuiContainer && ((JuiContainer) component2).type == ContainerType.COL)
		        .map(component2 -> (JuiContainer) component2)
		        .filter(col -> col.clientId().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}
	
	public JuiContainer tabs(String key) {
		
		return this.context.getLinkedMapContext().values().stream()
		        .filter(component -> component instanceof JuiContainer && ((JuiContainer) component).type == ContainerType.TABS)
		        .map(component -> (JuiContainer) component)
		        .flatMap(row -> row.context().getLinkedMapContext().values().stream())
		        .filter(component2 -> component2 instanceof JuiContainer && ((JuiContainer) component2).type == ContainerType.TAB)
		        .map(component2 -> (JuiContainer) component2)
		        .filter(col -> col.clientId().equals(key))
		        .findFirst()
		        .orElse(null);
		
	}

	public Collection<WebComponent> getComponents() {
		
		return this.context.getLinkedMapContext().values();
	}

	
	
	
	
	


}