package com.jui.html.elements;

import com.jui.html.WebContainer;
import com.jui.html.WebElement;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.extern.java.Log;

@Getter
@Setter
@Accessors(fluent = true)
@Log
public class Button extends WebElement {
	
    String label;
    String type;
	Boolean disabled;
    
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
    
	public void disable() {this.enableOrDisabled(true);}
	public void enable() {this.enableOrDisabled(false);}
	public void toogle() {this.enableOrDisabled(!this.disabled);}

	protected void enableOrDisabled(boolean disabled) {
		
		this.disabled = disabled;

		StringBuffer command = new StringBuffer();
		command.append("const button = document.getElementById(\"%s\");".formatted(this.clientId()));
		if ( disabled ) {
			command.append("button.disabled = true;");
			command.append("button.classList.add(\"disabled\");");
		} else {
			command.append("button.disabled = false;");
			command.append("button.classList.remove(\"disabled\");");				
		}
	
		this.backEndEvents().onServerUpdate(this, "change", command.toString());	
		
	}

}
