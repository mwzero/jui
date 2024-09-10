package com.jui.html;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.jui.templates.TemplateHelper;

import freemarker.template.TemplateException;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Setter
@Slf4j
public abstract class WebComponent {
	
	String Id;
	String key;
	String data;
	TemplateHelper engine;
	
	public String getValue() {
		return null;
	}
	
	public Integer getIntValue() {
		return null;
	}
	
	public String render() {	
		
		log.debug("Rendering [{}] [{}]", this.getId(), this.getKey());

		Map<String, Object> variables = this.getVariables();

		try {
			return this.getEngine().renderTemplate(this.getTemplateName(), variables);

		} catch (TemplateException | IOException e) {
			
			log.error("Error processing. Error [{}]", e.getLocalizedMessage());
			log.error("Error freeamrker template", e);
			
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return "";
	}
	
	public String getTemplateName() { 
		return "%s/%s".formatted(
				this.getClass().getPackage().getName()
				,this.getClass().getSimpleName());
	}
		
	public Map<String, Object> getVariables() {
		
		Map<String, Object> variables = new HashMap<>();
		variables.put("clientId", this.getKey());
		variables.put("key", this.getKey());
		
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
		return "postData%s();".formatted(this.getKey());
	}
	
}
