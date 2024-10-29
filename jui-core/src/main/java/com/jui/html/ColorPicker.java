package com.jui.html;

import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Log
public class ColorPicker extends WebComponent {
	
    private String label;


	public ColorPicker(String label) {
		
        this.label = label;
	}

	@Override
	public String render() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.getId(), this.getKey()));
		
		return """
				<div class="mb-3">
			      <label for="%s" class="form-label">%s</label>
			      <input type="color" id="%s" name="%s" class="form-control form-control-color" value="#CCCCCC">
			    </div>		
				""".formatted(this.getKey(),label,this.getKey(), this.getKey());
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}