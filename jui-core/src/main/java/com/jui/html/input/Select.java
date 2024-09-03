package com.jui.html.input;

import java.util.List;

import com.jui.html.WebComponent;
import com.st.SimpleColumn;
import com.st.SimpleRow;
import com.st.SimpleTable;

public class Select extends WebComponent {
	
    private String label;
    List<String> values;
    SimpleTable st;

	public Select(String label, List<String> values) {
		
        this.label = label;
        this.values = values;
	}
	
	public Select(String label, SimpleTable st) {
		
        this.label = label;
        this.st = st;
	}

	@Override
	public String render() {
		
		String html = "";
		
		
		if ( values != null ) {
		
			for ( String value : values) {
				String oneCheck = """ 
						<option value="%s">%s</option>
				""".formatted(value, value);
				
				html+=oneCheck;
					
			}
			
		} else {
			
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
			
		}
		
		return """
				<div class="mb-3">
		    		<label for="%s" class="form-label">%s</label>
		    		<select id="%s" name="%s" class="form-select form-control" aria-label="Default select example">%s</select>
				</div>
				""".formatted(this.getKey(), this.label, this.getKey(), this.getKey(), html);
		
		
	}
	
}
