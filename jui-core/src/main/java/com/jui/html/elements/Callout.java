package com.jui.html.elements;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class Callout extends WebElement {
	
	CalloutStatus status;
	String title;
	String text;
	
	public enum CalloutStatus {
		SUCCESS,
		INFO,
		WARNING,
		ERROR
	}
	
	public Callout(CalloutStatus status, String title, String text) {
		super("CallOut");
		
		this.status = status;
		this.title = title;
		this.text = text;
	}

	@Override
	public String getHtml() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		String statusString = "success";
		switch ( status) {
		case SUCCESS:
			statusString = "success";
			break;
		case INFO:
			statusString = "info";
			break;
		case WARNING:
			statusString = "warning";
			break;
		case ERROR:
			statusString = "error";
			break;
		}
		return """
				<div id="%s" class="callout callout-%s">
					<h4>%s</h4>
					%s
				</div>
				""".formatted(this.clientId(),statusString, this.title, this.text);
						
	}
}
