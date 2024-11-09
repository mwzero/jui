package com.jui.html;

import lombok.extern.java.Log;

@Log
public class JuiContainerRow extends JuiContainer {
	
	public JuiContainerRow(String key) {
		
		super(key);
			
	}
	
	@Override
	public String getHtml() {
		
		return """
				<div class="row" id="%s">{{content}}</div>
			   """.formatted(this.clientId());
	}	
}