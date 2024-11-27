package com.jui.html.elements;

import com.jui.html.WebComponent;

import lombok.extern.java.Log;

@Log
public class Text extends WebComponent {
	
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
		
		log.fine("Rendering [%s] [%s]".formatted(this.Id(), this.key()));
		
		if ( input ) {
			
			if ( readonly ) {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.key(),text,this.key(), this.key(), text);
						
			} else {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.key(), text, this.key(), this.key(), text);
				
			}
		} else {
			return "<p>" + text + "</p>";		
		}
		
	}
}
