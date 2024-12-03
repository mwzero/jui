package com.jui.html.elements.media;

import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Audio extends WebElement {
	
	String href;
	
	public Audio(String href) {
		super("Audio");
		
		this.href = href;
	}
	
	@Override
	public String getHtml() {
        
        return """
        		<audio controls class="w-100">
                    <source
		              src="%s"
		              type="audio/mpeg"
		            />
            	</audio>
				""".formatted(this.href);
    }


	
	

}
