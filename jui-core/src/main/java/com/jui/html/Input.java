package com.jui.html;

import com.jui.html.WebComponent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Input extends WebComponent {
	
    private String label;
    private boolean readonly = true;
    private boolean input = false;
    
    private String value;
    private String placeholder;

    
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
	public String render() {
		
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
