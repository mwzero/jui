package com.jui.html;

import java.util.HashMap;
import java.util.Map;

import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class WebAttributes {
	
	Map<String, Object> attributes;
	
	//sizing
	public final static String WIDTH_ATTRIBUTES = "width";
	public final static String HEIGHT_ATTRIBUTES = "height";
	
	//
	public final static String ACTIVE_ATTRIBUTES = "active";
	
	public WebAttributes() {
		
		attributes = new HashMap<String, Object>();
		
	}
	
	public boolean isActive() {
		
		if ( attributes.containsKey(ACTIVE_ATTRIBUTES)) {
			
			Boolean isActive = (Boolean) attributes.get(ACTIVE_ATTRIBUTES);
			return isActive;
			
		} 
		
		return false;
	}
	
	public int getAttributeAsInt(String attribute) {
		return (int) this.attributes.get(attribute);
	}
	
	public void addAttribute(String key, Object value) {
		this.attributes.put(key, value);
	}
	
	

}
