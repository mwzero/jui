package com.jui.html.elements;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(fluent = true)
public class Button extends WebElement {
	
    String label;
    String type;
    
    String clientClick;
    
    WebContainer container;

    public Button(String label, String type) {
    	super("Button"); 
    	this.label=label;
    	this.type=type;
    	this.clientClick = "";   
    }
    		
    public Button(String label, String type, String onClick) {
    	
    	super("Button");
    	
        this.label = label;
        this.clientClick = onClick;
        this.type = type;
    }

    public void onClick(String jsAction) {
    	this.clientClick = jsAction;
	}
    
	public void disable() {
		
		String command = """
				const button = document.getElementById("%s");
				button.disabled = true;
				button.classList.add("disabled");
				""".formatted(this.clientId());
		
		this.backEndEvents().onServerUpdate(this, "change", command);
		
	}
	
	public void enable() {

		String command = """
				const button = document.getElementById("%s");
				button.disabled = false;
				button.classList.remove("disabled");
				""".formatted(this.clientId());
		
		this.backEndEvents().onServerUpdate(this, "change", command);
	}

}
