package com.jui.html;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.jui.annotations.JuiAnnotationHelper;
import com.jui.helpers.MarkdownProcessor;
import com.jui.html.apis.TextElements;
import com.jui.html.tags.Button;
import com.jui.html.tags.CheckBox;
import com.jui.html.tags.ColorPicker;
import com.jui.html.tags.DatePicker;
import com.jui.html.tags.Divider;
import com.jui.html.tags.DropDownButton;
import com.jui.html.tags.FileInput;
import com.jui.html.tags.FormButton;
import com.jui.html.tags.Input;
import com.jui.html.tags.Radio;
import com.jui.html.tags.Select;
import com.jui.html.tags.Slider;
import com.jui.html.tags.Table;
import com.jui.html.tags.Text;
import com.jui.html.tags.UnorderedList;
import com.jui.html.tags.UnorderedListItem;
import com.jui.html.tags.FormButton.ButtonType;
import com.jui.html.tags.chart.ChartBar;
import com.jui.html.tags.chart.ChartLines;
import com.jui.html.tags.chart.ChartMap;
import com.st.DataFrame;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;
import lombok.AccessLevel;

@Log
@Getter
@Setter
@Accessors(fluent = true)
public class JuiContainer extends WebComponent implements AutoCloseable {
	
	String clientId;
	WebContext context;
	
	Map<String, Object> attributes;
	public final static String WIDTH_ATTRIBUTES = "width";
	
	enum ContainerType {
		DIV,
		ROW,
		COL
	};
	
	ContainerType type;
	
	@Getter(AccessLevel.NONE)
	TextElements textApis;
	
	public JuiContainer(String key) {  this(key, ContainerType.DIV, null); }
	public JuiContainer(String key, ContainerType type) { this(key, type, null);}
	
	public JuiContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		log.fine("New JuiContainer:" + key);
		
		this.clientId = key;
		this.type = type;
		if ( attributes == null ) attributes = new HashMap<String, Object>(); 
		else this.attributes = attributes;
		
		context = new WebContext();
		
		//Apis
		textApis = new TextElements(context);
	}
	
	@Override
	public String getHtml() {
		
		if ( type == ContainerType.COL ) {
			
			int width = (int) this.attributes.get(WIDTH_ATTRIBUTES);
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

	public Collection<WebComponent> getComponents() {
		
		return this.context.getLinkedMapContext().values();
	}

	
	
	public JuiContainer addContainer(String key) {
		
		JuiContainer container = new JuiContainer(key, ContainerType.DIV, null);
		context().add(container);
		
		return container;
	}

	public List<JuiContainer> columns(Map<String, Integer> of) {
		
		List<JuiContainer> cols = new ArrayList<JuiContainer>();
		
		JuiContainer row = new JuiContainer("", ContainerType.ROW, null);
		
		for (Entry<String, Integer> column : of.entrySet()) {
			
			JuiContainer col = new JuiContainer(column.getKey(), ContainerType.COL, Map.of( WIDTH_ATTRIBUTES, column.getValue()));
			row.context().add( col);
			cols.add(col);
			
		}
		this.context().add(row);
		
		return cols;
	}
	
	public JuiContainer columns(String key) {
		
		return this.context().getLinkedMapContext().values().stream()
		        .filter(component -> component instanceof JuiContainer && ((JuiContainer) component).type == ContainerType.ROW)
		        .map(component -> (JuiContainer) component)
		        .flatMap(row -> row.context().getLinkedMapContext().values().stream())
		        .filter(component2 -> component2 instanceof JuiContainer && ((JuiContainer) component2).type == ContainerType.COL)
		        .map(component2 -> (JuiContainer) component2)
		        .filter(col -> col.clientId().equals(key))
		        .findFirst()
		        .orElse(null);
		
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

	//Text Elements APIs
	public Text title(String text) { return textApis.title(text); }
	public Text header(String text, boolean divider) { return textApis.header(text, divider); }
	public Text header(String text, String dividerColor) { return textApis.header(text, dividerColor); }
	public Text subHeader(String text) { return textApis.subHeader(text); }
	public Text caption(String text) { return textApis.caption(text); }
	public Text markdown(String... args) { return textApis.markdown(args); }
	public String code(String text, String language, boolean line_numbers) { return textApis.code(text, language, line_numbers); }
	
	
	public ChartLines lines() {

		ChartLines lines = new ChartLines();
		context.add(lines);

		return lines;
		
	}
	
	public ChartBar bars() {
		
		ChartBar lines = new ChartBar();
		context.add(lines);
		
		return lines;
	}
	
	public ChartMap map() {
		
		ChartMap map = new ChartMap();
		context.add(map);
		
		return map;
	}
}