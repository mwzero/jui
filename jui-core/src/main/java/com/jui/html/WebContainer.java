package com.jui.html;

import java.util.Map;

import com.jui.html.apis.ChartElements;
import com.jui.html.apis.ContainerElements;
import com.jui.html.apis.ContainerElements.ContainerType;
import com.jui.html.apis.DataElements;
import com.jui.html.apis.InputButtonElements;
import com.jui.html.apis.InputSelectionElements;
import com.jui.html.apis.InputTextElements;
import com.jui.html.apis.MediaElements;
import com.jui.html.apis.OtherElements;
import com.jui.html.apis.StatusElements;
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
    private final DataElements dataApis;
	
	@Delegate 
	private final ChartElements chartElements;
	
	@Delegate 
	private final InputTextElements inputTextElements;

	@Delegate 
	private final InputButtonElements inputButtonElements;
	
	@Delegate 
	private final InputSelectionElements inputSelectionElements;
	
	@Delegate 
	private final OtherElements otherElements;
	
	@Delegate
	private final MediaElements mediaElements;
	
	@Delegate
	private final StatusElements statusElements;
	
	
	public WebContainer(String key) {  this(key, ContainerType.DIV, null); }
	public WebContainer(String key, ContainerType type) { this(key, type, null);}
	
	public WebContainer(String key, ContainerType type, Map<String, Object> attributes) {
		
		super("Div", key, attributes);
		this.type = type;
		
		dataApis = new DataElements(this.webContext());
		textApis = new TextElements(this.webContext());
		containerApis = new ContainerElements(this.webContext());
		chartElements = new ChartElements(this.webContext());
		inputButtonElements = new InputButtonElements(this.webContext());
		inputSelectionElements = new InputSelectionElements(this.webContext());
		inputTextElements = new InputTextElements(this.webContext());

		otherElements = new OtherElements(this.webContext());
		mediaElements = new MediaElements(this.webContext());
		statusElements = new StatusElements(this.webContext());
		
		
	}
	
	
	@Override
	public String getHtml() {
		
		if ( type == ContainerType.COL ) {
			
			int width = this.getAttributeAsInt(WebElementAttributes.WIDTH_ATTRIBUTES);
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
			
		} else if ( type == ContainerType.POPOVER ) {
			
			StringBuffer sb = new StringBuffer();
			sb.append("""
					<div hidden >
		                <div data-name="popover-content-%s">
					        {{content-%s}}
		                </div>
		            </div>
            
					<a id="%s" tabindex="0" class="btn btn-lg btn-danger" role="button" data-bs-toggle="popover" title="%s" >Html inside popover</a>
					""".formatted(clientId, clientId, clientId, key));
					
			sb.append("""
					<script>
						var options = {
						        html: true,
						        title: "Optional: HELLO(Will overide the default-the inline title)",
						        //html element
						        //content: $("#popover-content-%s")
						        content: document.querySelector('[data-name="popover-content-%s"]')
						        //content: $('[data-name="popover-content-%s"]')
						        //Doing below won't work. Shows title only
						        //content: $("#popover-content-%s").html()
						
						    };
					    var exampleEl = document.getElementById('%s');
					    var popover = new bootstrap.Popover(exampleEl, options);
					</script>
					""".formatted(clientId, clientId,clientId, clientId, clientId));
					
			return sb.toString();
			
		} else if ( type == ContainerType.DIALOG ) {
			
			StringBuffer sb = new StringBuffer();
			sb.append("""
				<button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#modal-%s">
				  %s
				</button>
				
				<!-- Modal -->
				<div class="modal fade" id="modal-%s" tabindex="-1" aria-labelledby="modalLabel-%s" aria-hidden="true">
				  <div class="modal-dialog">
				    <div class="modal-content">
				      <div class="modal-header">
				        <h5 class="modal-title" id="modalLabel-%s">Modal title</h5>
				        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
				      </div>
				      <div class="modal-body">
				        {{content-%s}}
				      </div>
				      <div class="modal-footer">
				        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Close</button>
				        <button type="button" class="btn btn-primary">Save changes</button>
				      </div>
				    </div>
				  </div>
				</div>
					""".formatted(clientId, key,clientId, clientId, clientId,clientId));
					
			return sb.toString();
			
		} else { 
		
		
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