package com.jui.html;

import java.util.Map;

import com.jui.html.apis.ChartElements;
import com.jui.html.apis.ContainerElements;
import com.jui.html.apis.ContainerElements.ContainerType;
import com.jui.html.apis.InputButtonElements;
import com.jui.html.apis.InputSelectionElements;
import com.jui.html.apis.OtherElements;
import com.jui.html.apis.TextElements;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;

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
	
	
	public WebContainer(String key) {  this(key, ContainerType.DIV, null); }
	public WebContainer(String key, ContainerType type) { this(key, type, null);}
	
	public WebContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		super("Div", key, attributes);
		this.type = type;
		
		
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
			
			int width = this.getAttributeAsInt(WebAttributes.WIDTH_ATTRIBUTES);
			return """
					<div id="%s" class="col-%s" >{{content-%s}}</div>
				   """.formatted(this.clientId(), width, this.clientId());
		} else if ( type == ContainerType.DIV ) {
			
			return """
					<div id="%s">{{content-%s}}</div>
				   """.formatted(clientId, clientId);
			
		} else if ( type == ContainerType.ROW ) { 
			
			return """
					<div class="row" id="%s">{{content-%s}}</div>
				   """.formatted(clientId, clientId);
			
		} else if ( type == ContainerType.TABS ) { 
			
			String html = """
					<nav>
					  <div class="nav nav-tabs" id="%s" role="tablist">
					  {{child}}
					  </div>
					</nav>
					<div class="tab-content" id="nav-tabContent">
					{{content-%s}}
					</div>
					""".formatted(clientId, clientId);
			
			StringBuffer sb = new StringBuffer();
			for ( String child : this.webContext.childrens.keySet()) {
				
				WebElement we = this.webContext.childrens.get(child);
				
				if ( we.isActive()) {
					
					sb.append(""" 
							 <button class="nav-link active" id="nav-%s-tab" data-bs-toggle="tab" data-bs-target="#nav-%s" type="button" role="tab" aria-controls="nav-%s" aria-selected="false">%s</button>
							""".formatted(clientId, we.clientId, we.clientId, we.key));
					
				} else {
					sb.append("""
						 <button class="nav-link" id="nav-%s-tab" data-bs-toggle="tab" data-bs-target="#nav-%s" type="button" role="tab" aria-controls="nav-%s" aria-selected="false">%s</button>
						""".formatted(clientId, we.clientId, we.clientId, we.key));
				}
			}
			
			return html.replace("{{child}}", sb.toString());
			
			
		} else if ( type == ContainerType.TAB ) { 
			
			if ( isActive()) {
				return """
						<div class="tab-pane fade show active" id="nav-%s" role="tabpanel" aria-labelledby="nav-%s-tab">
								{{content-%s}}
						</div>
					""".formatted(clientId, clientId, clientId);
				
			} else {
				return """
					<div class="tab-pane fade" id="nav-%s" role="tabpanel" aria-labelledby="nav-%s-tab">
							{{content-%s}}
					</div>
				""".formatted(clientId, clientId, clientId);
			}
			
			
		} else if ( type == ContainerType.EXPANDER ) {
			
			StringBuffer sb = new StringBuffer();
			sb.append("""
					<p>
					  <a class="btn btn-primary" data-bs-toggle="collapse" href="#collapse%s" role="button" aria-expanded="false" aria-controls="collapse%s">
					    %s
					  </a>
					</p>
					""".formatted(clientId, clientId, key));
					
			sb.append("""
					<div class="collapse" id="collapse%s">
					  <div class="card card-body">
					    {{content-%s}}
					  </div>
					</div>
					""".formatted(clientId, clientId));
					
			return sb.toString();
			
		}else { 
		
		
			return """
				<div class="row" id="%s">{{content-%s}}</div>
			   """.formatted(clientId, clientId);
		
		}
		
	}
	@Override
	public void close() throws Exception {
		
		//mf: No resources to free. Leave this method empty.
		
	}
}