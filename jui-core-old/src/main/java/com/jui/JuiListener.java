package com.jui;

import java.util.Map;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class JuiListener {

    public static final JuiListener listener = new JuiListener();

    public WebElement executeServerAction(String id, String action, Map<String, Object> payload) throws Exception{
		
        log.fine("execute action %s for id %s".formatted(action, id));
		WebElement  component = findWebElement(id);
		if ( component == null ) {
            log.warning("WebElement not found for id %s".formatted(id));
        } else {
            log.fine("WebElement found for id %s".formatted(id));
			component.onUpdate(action, payload);
			return component;
		}
		
		return null;
		
	}

    //TODO: Implement this method correcly because the search have to be recorsive
    private WebElement findWebElement(String id) {
     
        return JuiApp.jui.webContext().getLinkedMapContext().get(id);
    }

}
