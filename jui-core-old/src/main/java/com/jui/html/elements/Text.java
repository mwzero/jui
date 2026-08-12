package com.jui.html.elements;

import com.jui.html.WebElement;

import lombok.extern.java.Log;

@Log
public class Text extends WebElement {
	
    private String text;
    private boolean readonly = true;
    private boolean input = false;

	public Text(String text, boolean input, boolean readonly) {
		super("Text");
		
        this.text = text;
        this.readonly = readonly;
        this.input = input;
	}

	@Override
	public String getHtml() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.clientId()));
		
		if ( input ) {
			
			if ( readonly ) {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.clientId(),text,this.clientId(), this.clientId(), text);
						
			} else {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.clientId(), text, this.clientId(), this.clientId(), text);
				
			}
		} else {
			return text;		
		}
		
	}
}
