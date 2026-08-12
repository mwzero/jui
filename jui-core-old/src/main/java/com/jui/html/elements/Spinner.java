package com.jui.html.elements;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class Spinner extends WebElement {
	
	String text;
	
	public Spinner(String text) {
		super("Spinner");
		
		this.text = text;
	}
	
	@Override
	public String getHtml() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		return """
				<div id="%s" class="spinner-border" style="width: 3rem; height: 3rem;" role="status">
					<span class="visually-hidden">%s</span>
				</div>
				""".formatted(this.clientId(), this.text);
						
	}
}
