package com.jui.html.elements;

import java.util.Map;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class ProgressBar extends WebElement {
	
	int value;
	String text;
	
	
	public ProgressBar(int value, String text) {
		super("ProgressBar");
		
		this.text = text;
		this.value = value;
	}
	
	public void clear() {
		progress(0, "progress 0%");
	}
	
	public void progress(int value, String text) {
		
		this.text = text;
		this.value = value;
		
		String command = """
				var progressBar = document.getElementById("%s-progress-bar");
				progressBar.style.width = "%d%%";
				progressBar.setAttribute("aria-valuenow", "%d");
				""".formatted(this.clientId(), this.value, this.value);
		
		this.onServerUpdate(this, "change", command);
		
	}
	
	@Override
	public Map<String, Object> getMapValue() {
		return Map.of("value", value);
	}

	@Override
	public String getHtml() {
		
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		return """
				<div id="%s" class="progress">
					<div id="%s-progress-bar" class="progress-bar" role="progressbar" style="width: %d%%" aria-valuenow="%d" aria-valuemin="0" aria-valuemax="100"></div>
				</div>
				""".formatted(this.clientId(), this.clientId(), this.value, this.value);
						
	}

	
}
