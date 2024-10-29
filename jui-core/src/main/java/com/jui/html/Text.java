package com.jui.html;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class Text extends WebComponent {
	
    private String text;
    private boolean readonly = true;
    private boolean input = false;

	public Text(String text, boolean input, boolean readonly) {
		
		this.setId("Text");
		
        this.text = text;
        this.readonly = readonly;
        this.input = input;
	}

	@Override
	public String render() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.getId(), this.getKey()));
		
		if ( input ) {
			
			if ( readonly ) {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.getKey(),text,this.getKey(), this.getKey(), text);
						
			} else {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s">
					    </div>		
						""".formatted(this.getKey(), text, this.getKey(), this.getKey(), text);
				
			}
		} else {
			return "<p>" + text + "</p>";		
		}
		
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
