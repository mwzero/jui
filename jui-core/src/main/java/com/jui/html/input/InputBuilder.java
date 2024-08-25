package com.jui.html.input;

import com.jui.html.WebComponent;

import lombok.Builder;

@Builder
public class InputBuilder {
	
	String label;
	String value;
	String placeholder; 
	boolean input;
	boolean readonly;
	
	//
	WebComponent c_label;
	WebComponent c_value;
	WebComponent c_placeholder;

}
