package com.jui.html.elements.media;

import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Video extends WebElement {
	
	public enum Format {
		Format_21by9,
		Format_16b9,
		Format_4by3,
		Format_1by1
		
	}
	
	String href;
	Format format;
	boolean allowFullScreen;
	
	public Video(String href, Format format, Boolean allowFullScreen) {
		super("Video");
		
		this.href = href;
		this.format = format;
		this.allowFullScreen = allowFullScreen;
	}
	
	@Override
	public String getHtml() {
        
        return """
        		<div class="embed-responsive embed-responsive-16by9">
	        		<video class="embed-responsive-item" controls allowfullscreen>
	        			<source src="%s" type="video/webm" />
	        		</video>
	        	</div>
				""".formatted(this.href);
    }


	
	

}
