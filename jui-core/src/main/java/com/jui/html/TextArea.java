package com.jui.html;

import com.jui.html.WebComponent;

public class TextArea extends WebComponent {
	
    private String text;
    private boolean readonly = true;
    private int rows = 3;

	public TextArea(String text, int rows, boolean readonly) {
		
        this.text = text;
        this.readonly = readonly;
        this.rows = rows;
	}

	@Override
	public String render() {
		
		return """
				<div class="mb-3">
			      <label for="%s" class="form-label">%s</label>
			      <textarea  type="text" id="%s" name="%s" class="form-control" rows="%d"></textarea>
			    </div>		
				""".formatted(this.getKey(),text,this.getKey(), this.getKey(), rows);
						
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
