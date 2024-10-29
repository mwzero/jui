package com.jui.html;

import lombok.extern.java.Log;

@Log
public class DatePicker extends WebComponent {
	
    private String label;


	public DatePicker(String label) {
		
        this.label = label;
	}

	@Override
	public String render() {
		
		log.fine("Rendering [%s] [%s]".formatted(this.getId(), this.getKey()));
		
		return """
				<div class="mb-3">
			      <label for="%s" class="form-label">%s</label>
			      <input type="date" id="%s" name="%s" class="form-control">
			    </div>		
				""".formatted(this.getKey(),label,this.getKey(), this.getKey());
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
		
	