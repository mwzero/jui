package com.jui.builders;

import com.jui.WebContext;

public abstract class BaseBuilder {
	
	WebContext context;
	
	public BaseBuilder(WebContext context) {
		this.context = context;
	}

}
