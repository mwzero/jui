package com.jui.html.tags;

import java.util.List;

import com.jui.html.WebComponent;

public class Radio extends WebComponent {
	
    private String label;
    List<String> values;

	public Radio(String label, List<String> values) {
		
        this.label = label;
        this.values = values;
	}

	@Override
	public String getHtml() {
		
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
