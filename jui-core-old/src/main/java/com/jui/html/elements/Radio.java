package com.jui.html.elements;

import java.util.List;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class Radio extends WebElement {
	
    private String label;
    List<String> values;

	public Radio(String label, List<String> values) {
		super("Radio");
		
        this.label = label;
        this.values = values;
	}

	@Override
	public String getHtml() {
	
		log.info("label is never used" + label);

		String html = "";
		
		int i=0;
		for ( String value : values) {
			i++;
			String oneCheck = """ 
			<div class="form-check">
			  <input class="form-check-input" type="radio" name="%s" id="%s">
			  <label class="form-check-label" for="%s">
			    %s
			  </label>
			</div>
			""".formatted(this.clientId(), this.clientId() + "_" + i, this.clientId() + "_" + i, value);
			
			html+=oneCheck;
				
		}
		
		return html;
	}
}
