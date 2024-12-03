package com.jui.html;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import lombok.extern.java.Log;

@Getter
@Setter
@Accessors(fluent = true)
@Log
public abstract class WebElement {
	
	//web context 
	WebElementContext webContext;
	
	
	@Delegate
	WebAttributes attributes;
	
	/**
	 * Specify Html Element Id like: div, button, slider, etc..
	 */
	String Id;
	
	/**
	 * Specify the HTML id attribute, calculated by the web context when a new element is added.
	 */
	String clientId;
	
	
	/**
	 * Specify Key assigned by the user. Used for cols, tabs, etc..
	 */
	String key;
	
	protected Runnable onServerSide;
	
	public WebElement(String id) {
		
		log.fine("New WebComponent:" + id);
		this.Id(id);
		
		attributes = new WebAttributes(); 
		webContext = new WebElementContext();
		
		
	}
	
	public WebElement(String id, String key, Map<String, Object> attributes) {
		
		this(id);
		this.key = key;
		
		if ( attributes != null)
			this.attributes.attributes = attributes;
		
	}
	
	
	public void executeServerAction() {
		onServerSide.run();	
	}
	
	public String getValue() {
		return null;
	}
	
	public Integer getIntValue() {
		return null;
	}
	
	public void preProcessBindingAndRelations() {;}
	
	public String getHtml() { return null;}
	
	public String getTemplateName() {
		
		return "%s/%s".formatted(
				this.getClass().getPackage().getName()
				,this.getClass().getSimpleName());
	}
		
	public Map<String, Object> getVariables() {
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("clientId", this.clientId());
		variables.put("juiComponent", this);
		if ( onServerSide != null ) {
			variables.put("onServerSide", "server-side");
		}
		
		for ( Field field : this.getClass().getDeclaredFields()) {
			
            field.setAccessible(true); // Rende il campo accessibile

            // Recuperare il tipo del campo
            Class<?> fieldType = field.getType();

			try {

	            if (fieldType == int.class) {
	                int value = field.getInt(this.getClass());
	                variables.put(field.getName(), value);
	            } else if (fieldType == boolean.class) {
	                boolean value = field.getBoolean(this);
	                variables.put(field.getName(), value);
	            } else if (fieldType == double.class) {
	                double value = field.getDouble(this);
	                variables.put(field.getName(), value);
	            } else if (fieldType == String.class) {
	                String value = (String) field.get(this);
	                variables.put(field.getName(), value);
	            } else {
	                Object value = field.get(this); // Recupera il valore generico
	                variables.put(field.getName(), value);
	            }
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		return variables;
	}
	
	public String getPostData() {return null;}
	
	public WebElement add(WebElement component) {
		
		//add component to web context
		//generate a key and assign to it
		this.webContext().add(component);
		
		return component;
	}
}
