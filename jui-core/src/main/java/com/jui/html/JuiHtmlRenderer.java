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
	
	protected String render (WebComponent component) {
		
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
	
	protected String render (JuiContainer container ) {
		
		StringBuilder html = new StringBuilder();
			
		for (WebComponent component : container.getComponents()) {
			
			log.fine("Rendering [%s] [%s]".formatted(component.getId(), component.getKey()));
	
			if ( component instanceof JuiContainer ) {
				
				String containerEnvelop = render(component);
				String containerContents = render((JuiContainer) component);
				containerEnvelop = containerEnvelop.replace("{{content}}", containerContents);
				
				html.append(containerEnvelop);
				
			}
				
			else html.append(render(component));
				
		}
		
		html.append( buildScripts(container) );
		
		return html.toString();
	}
	
	private String buildScripts(JuiContainer container) {
		
        String elementMappingJson = Utils.buildJsonString(container.getContext().relations, "source", "commands");
        String elementPostDataJson = Utils.buildJsonString(container.getContext().elementPostData, "source", "commands");

        return """
                <script>
                    elementMapping=%s;
                    elementPostData=%s;
                </script>
                """.formatted(elementMappingJson, elementPostDataJson);
    }
	
	
	public JuiContent process(JuiContainer main, JuiContainer sidebar) {
		
		JuiContent content = new JuiContent();
		content.setMain(render(main));
		
		if ( sidebar.getContext().getLinkedMapContext() != null) {
			
			content.setSidebar(render(sidebar));
		} else
			content.setSidebar("");
		
		return content;
	}
}
