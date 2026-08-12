package com.jui.html.elements.media;

import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Image extends WebElement {
	
    String href;
    String caption;

    public Image(String href, String caption) {
    	super("Image");
    	
    	this.href = href;
    	this.caption = caption;
    }
    
    @Override
	public String getHtml() {
        
        return """
        		<img src="%s" class="img-fluid" alt="%s">
				""".formatted(this.href, this.caption);
    }
}
    		

