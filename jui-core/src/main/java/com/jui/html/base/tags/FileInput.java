package com.jui.html.base.tags;

import java.util.List;

import com.jui.html.WebComponent;

public class FileInput extends WebComponent {
	
    private String label;


	public FileInput(String label) {
		
        this.label = label;
	}

	@Override
	public String render() {
		
		String html = """
				<div class="mb-3">
					<label for="%s" class="form-label">%s</label>
					<input class="form-control" type="file" id="%s" multiple>
				</div>
			""".formatted(this.getKey(), label, this.getKey());
			
		return html;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
		
	
}