package com.jui.html.apis;

import com.jui.html.WebElementContext;
import java.util.UUID;

public class BaseElements {
	
	WebElementContext context;
	
	public BaseElements(WebElementContext context) {
		
		this.context = context;
		
	}

	protected String buildUniqueIDGenerator() {
        UUID uniqueID = UUID.randomUUID();
        String idString = uniqueID.toString();
		return idString.replace("-", "_");
    }
}
