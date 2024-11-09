package com.jui.html;

import lombok.extern.java.Log;

@Log
public class JuiContainerCol extends JuiContainer {
	
	int width;
	
	public JuiContainerCol(String key, int width) {
		
		super(key);
		this.width = width;
			
	}
	
	@Override
	public String getHtml() {
		
		return """
				<div id="%s" class="col-%s" >{{content}}</div>
			   """.formatted(this.clientId(), this.width);
	}	
}