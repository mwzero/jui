package com.jui.builders;

import com.jui.html.WebComponent;

import lombok.Builder;

@Builder
public class InputAttributes {
	
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
