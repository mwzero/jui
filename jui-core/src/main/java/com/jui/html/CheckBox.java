package com.jui.html;

import java.util.List;

import com.jui.html.WebComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CheckBox extends WebComponent {
	
    private String label;
    List<String> values;

	public CheckBox(String label, List<String> values) {
		
        this.label = label;
        this.values = values;
	}

	@Override
	public String render() {
		
		log.debug("Rendering [{}] [{}]", this.getId(), this.getKey());
		
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
