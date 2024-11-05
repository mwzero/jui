package com.jui.html.base.builders;

import com.jui.html.WebContext;

public abstract class BaseBuilder {
	
	protected WebContext context;
	
	public BaseBuilder(WebContext context) {
		this.context = context;
	}

}
