package com.jui.html;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.java.Log;

@Getter
@Setter
@Log
public abstract class WebComponent {
	
	//web context 
	WebContext webContext;
	
	Map<String, Object> attributes;
	
	String clientId;
	String Id;
	String key;
	String data;
	
	protected Runnable onServerSide;
	
	public WebComponent() {
		
		log.fine("New WebComponent");
		attributes = new HashMap<String, Object>(); 
		webContext = new WebContext();
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
		variables.put("clientId", this.getKey());
		variables.put("key", this.getKey());
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

	public String getPostData() {
		return null;
		//return "postData%s();".formatted(this.getKey());
	}
	
}
