package com.jui.html.tags;

import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Getter
@Setter
@Accessors(fluent = true)
@Log
public class Input extends WebComponent {
	
    String label;
    boolean readonly = true;
    boolean input = false;
    
    String value;
    String placeholder;
    
    //
	WebComponent c_label;
	WebComponent c_value;
	WebComponent c_placeholder;

    
    public Input() {
	}

	public Input(String label, boolean input, boolean readonly, String value, String placeholder) {
		
        this.label = label;
        this.readonly = readonly;
        this.input = input;
        
        this.value = value;
        this.placeholder = placeholder;
		
	}
	
	@Override
	public String getValue() {
		return value;
	}

	@Override
	public void preProcessBindingAndRelations() {
		
		if ( c_label != null ) {
			//context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(builder.c_lat.getKey()));
			this.getWebContext().addRelations(getKey(), "document.getElementById('%s').value=value".formatted(c_label.getKey()));
		} 
		
		if ( c_value != null ) {
			value(c_value.getValue());
			//context.addRelations(mapChart.getKey(), "document.getElementById('%s').value=value.lat".formatted(builder.c_lat.getKey()));
			this.getWebContext().addRelations(c_value.getKey(), "document.getElementById('%s').value=value".formatted(getKey()));
		} 
	}
	
	@Override
	public String getHtml() {
		
		if ( input ) {
			
			if ( readonly ) {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s" value="%s"></input>
					    </div>		
						""".formatted(this.getKey(),label,this.getKey(), this.getKey(), placeholder, value);
						
			} else {
				
				return """
						<div class="mb-3">
					      <label for="%s" class="form-label">%s</label>
					      <input type="text" id="%s" name="%s" class="form-control" placeholder="%s" value="%s"></input>
					    </div>		
						""".formatted(this.getKey(), label, this.getKey(), this.getKey(), placeholder, value);
				
			}
		} else {
			return "<p>" + label + "</p>";		
		}
		
	}

	@Override
	public String getPostData() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
