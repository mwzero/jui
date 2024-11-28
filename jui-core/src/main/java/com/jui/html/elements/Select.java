package com.jui.html.elements;

import java.util.List;

import com.jui.html.WebElement;
import com.st.DataFrame;

public class Select extends WebElement {
	
    private String label;
    List<String> values;
    DataFrame df;

	public Select(String label, List<String> values) {
		super("Select");
        this.label = label;
        this.values = values;
	}
	
	public Select(String label, DataFrame df) {
		super("Select");
        this.label = label;
        this.df = df;
	}

	@Override
	public String getHtml() {
		
		String html = "";
		
		
		if ( values != null ) {
		
			for ( String value : values) {
				String oneCheck = """ 
						<option value="%s">%s</option>
				""".formatted(value, value);
				
				html+=oneCheck;
					
			}
			
		} else {
			
			/*
			for ( SimpleRow row: st.getRows()) {
				
				String optionValue = "";
				for ( SimpleColumn col : st.getColumns()) {
					optionValue = (String) row.getValues().get(0);
				}
				
				String oneCheck = """ 
						<option value="%s">%s</option>
				""".formatted(optionValue, row.getValues().get(0));
				
				html+=oneCheck;
					
			}
			*/
			
		}
		
		return """
				<div class="mb-3">
		    		<label for="%s" class="form-label">%s</label>
		    		<select id="%s" name="%s" class="form-select form-control" aria-label="Default select example">%s</select>
				</div>
				""".formatted(this.clientId(), this.label, this.clientId(), this.clientId(), html);
		
		
	}
	
}
