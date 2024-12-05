package com.jui.html.elements;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class ProgressBar extends WebElement {
	
	int value;
	String text;
	
	
	public ProgressBar(int value, String text) {
		super("CallOut");
		
		this.text = text;
		this.value = value;
	}
	
	public void progress(int value, String text) {
		
		//socket comunication
		
	}

	@Override
	public String getHtml() {
		
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		return """
				<div id="%s" class="progress">
					<div class="progress-bar" role="progressbar" style="width: %d%%" aria-valuenow="%d" aria-valuemin="0" aria-valuemax="100"></div>
				</div>
				""".formatted(this.clientId(), this.value, this.value);
						
	}
}
