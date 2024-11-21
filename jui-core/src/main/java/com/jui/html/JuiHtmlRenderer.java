package com.jui.html;

import java.io.IOException;
import java.util.Map;

import com.jui.helpers.TemplateHelper;
import com.jui.model.JuiContent;
import com.jui.utils.Utils;

import lombok.extern.java.Log;

@Log
public class JuiHtmlRenderer {
	
	TemplateHelper engine;
	
	public JuiHtmlRenderer() {

		log.info("Initializing Rendering engine");
        this.engine = initializeTemplateHelper();
	}
	
	
	private TemplateHelper initializeTemplateHelper() {
        
		try {
            return new TemplateHelper(true, null);
            
        } catch (IOException e) {
            log.severe("Failed to initialize TemplateEngine: " + e.getLocalizedMessage());
            throw new IllegalStateException("Template engine initialization failed", e);
        }
    }
	
	protected String renderWebComponent (WebComponent component) {
		
		StringBuilder html = new StringBuilder();
		
		component.preProcessBindingAndRelations();
		
		if ( component.getHtml() != null ) html.append( component.getHtml() );
		else {
			Map<String, Object> variables = component.getVariables();

			try {
				html.append( engine.renderTemplate(component.getTemplateName(), variables));

			} catch ( Exception e) {
				
				log.severe(e.getLocalizedMessage());
				
			}
		}  
		
		return html.toString();
	}
	
	public String render (JuiContainer container ) {
		
		StringBuilder html = new StringBuilder();
			
		for (WebComponent component : container.getComponents()) {
			
			log.fine("Rendering [%s] [%s]".formatted(component.getId(), component.getKey()));
	
			if ( component instanceof JuiContainer ) {
				
				String containerEnvelop = renderWebComponent(component);
				String containerContents = render((JuiContainer) component);
				containerEnvelop = containerEnvelop.replace("{{content}}", containerContents);
				
				html.append(containerEnvelop);
				
			}
				
			else html.append(renderWebComponent(component));
				
		}
		
		html.append( buildScripts(container) );
		
		return html.toString();
	}
	
	private String buildScripts(JuiContainer container) {
		
        String elementMappingJson = Utils.buildJsonString(container.context().relations, "source", "commands");
        String elementPostDataJson = Utils.buildJsonString(container.context().elementPostData, "source", "commands");

        return """
                <script>
                    elementMapping=%s;
                    elementPostData=%s;
                </script>
                """.formatted(elementMappingJson, elementPostDataJson);
    }
}
