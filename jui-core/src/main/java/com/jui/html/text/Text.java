package com.jui.html.text;

import com.jui.html.WebComponent;

public class Text extends WebComponent {
	
    private String text;
    private boolean readonly = true;
    private boolean input = false;

	public Text(String text, boolean input, boolean readonly) {
		
        this.text = text;
        this.readonly = readonly;
        this.input = input;
	}

	@Override
	public String render() {
		
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
