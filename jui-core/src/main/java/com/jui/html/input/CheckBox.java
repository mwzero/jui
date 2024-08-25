package com.jui.html.input;

import java.util.List;

import com.jui.html.WebComponent;

public class CheckBox extends WebComponent {
	
    private String label;
    List<String> values;

	public CheckBox(String label, List<String> values) {
		
        this.label = label;
        this.values = values;
	}

	@Override
	public String render() {
		
		String html = "";
		
		int i=0;
		for ( String value : values) {
			i++;
			String oneCheck = """ 
			<div class="form-check">
			  <input class="form-check-input" type="checkbox" name="%s" id="%s">
			  <label class="form-check-label" for="%s">
			    %s
			  </label>
			</div>
			""".formatted(this.getKey(), this.getKey() + "_" + i, this.getKey() + "_" + i, value);
			
			html+=oneCheck;
				
		}
		
		return html;
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
		
	
}
