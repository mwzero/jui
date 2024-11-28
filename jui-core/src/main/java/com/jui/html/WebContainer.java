package com.jui.html;

import java.util.ArrayList;
import java.util.Collection;

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
public class WebContainer extends WebElement implements AutoCloseable {
	
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
	
	
	public WebContainer(String clientId) {  this(clientId, ContainerType.DIV, null); }
	public WebContainer(String clientId, ContainerType type) { this(clientId, type, null);}
	public WebContainer(String clientId, ContainerType type, Map<String, Object> attributes) {
		
		super("Div", clientId, attributes);
		this.type = type;
		
		
		//Apis Delegates
		textApis = new TextElements(this.webContext());
		containerApis = new ContainerElements(this.webContext());
		chartElements = new ChartElements(this.webContext());
		inputButtonElements = new InputButtonElements(this.webContext());
		inputSelectionElements = new InputSelectionElements(this.webContext());
		otherElements = new OtherElements(this.webContext());
		
	}
	
	@Override
	public String getHtml() {
		
		if ( type == ContainerType.COL ) {
			
			int width = (int) this.attributes.get(WebAttributes.WIDTH_ATTRIBUTES);
			return """
					<div id="%s" class="col-%s" >{{content}}</div>
				   """.formatted(this.clientId(), width);
		} else {
			
			return """
					<div class="row" id="%s">{{content}}</div>
				   """.formatted(clientId);
			
		}
		
	}
	
	@Override
	public void close() throws Exception {
		// TODO Auto-generated method stub
		
	}

}