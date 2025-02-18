package com.jui;

import java.util.Map;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;
import com.jui.model.JuiContent;
import com.jui.processors.TemplateEngine;
import com.jui.utils.Utils;

import lombok.extern.java.Log;

@Log
public class JuiHtmlRenderer {
	
	TemplateEngine engine;
	
	public JuiHtmlRenderer() {

		log.fine("Initializing Rendering engine");
		this.engine = new TemplateEngine(true, null);
    }
	
	public JuiContent render() {
			
		JuiContent content = new JuiContent();
		content.setMain( renderWebContainer(JuiApp.jui));
		
		if ( JuiApp.jui.sidebar.webContext().getLinkedMapContext() != null) {
			
			content.setSidebar(renderWebContainer(JuiApp.jui.sidebar));
			
		} else
			content.setSidebar("");
		
		return content;
	}

	private String renderWebContainer (WebContainer container ) {
		
		StringBuilder html = new StringBuilder();
			
		for (WebElement component : container.getComponents()) {
			
			log.fine("Rendering [%s] [%s]".formatted(component.Id(), component.clientId()));
	
			if ( component instanceof WebContainer ) {
				
				String containerEnvelop = renderWebComponent(component);
				String containerContents = renderWebContainer((WebContainer) component);
				containerEnvelop = containerEnvelop.replace("{{content-%s}}".formatted(component.clientId()), containerContents);
				
				html.append(containerEnvelop);
				
			}
				
			else html.append(renderWebComponent(component));
				
		}
		
		html.append( buildScripts(container) );
		
		return html.toString();
	}
	
	private String renderWebComponent (WebElement component) {
		
		StringBuilder html = new StringBuilder();
		
		component.preProcessBindingAndRelations();
		
		if ( component.getHtml() != null ) html.append( component.getHtml() );
		else {
			Map<String, Object> variables = component.getVariables();

			try {
				html.append( engine.renderFromFile(component.getTemplateName(), variables));
				

			} catch ( Exception e) {
				
				log.severe(e.getLocalizedMessage());
				
			}
			
		}  
		html.append(buildScripts(component));
		return html.toString();
	}
	
	private String buildScripts(WebElement container) {

		if ( ( container.webContext().relations.size() == 0 ) && (container.webContext().elementPostData.size() == 0 ) )
			return "";
        
        StringBuffer script = new StringBuffer();
        script.append("<script>");
        if ( container.webContext().relations.size() > 0 ) {
        	String elementMappingJson = Utils.buildJsonString(container.webContext().relations, "source", "commands");
        	script.append("elementMapping=elementMapping.concat(%s)".formatted(elementMappingJson));
        }
        
        if ( container.webContext().elementPostData.size() > 0 ) {
        	String elementPostDataJson = Utils.buildJsonString(container.webContext().elementPostData, "source", "commands");
        	script.append("elementPostData = elementPostData.concat(%s)".formatted(elementPostDataJson));
        }
        
        script.append("</script>");
        
        return script.toString();
    }
}
