package com.jui.annotations;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;

import com.jui.WebContext;
import com.jui.html.Slider;
import com.jui.html.Text;
import com.jui.html.WebComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JuiAnnotationHelper {
	
	public static void write ( WebContext context, Object obj ) {
		
		for ( Field field : obj.getClass().getDeclaredFields()) {
			
			WebComponent component = null;
			if (field.getAnnotations().length > 0) {
				
				Annotation[] annotations = field.getAnnotations();
				
				for ( Annotation annotation : annotations ) {
					
					if ( annotation instanceof JuiSlider) {
						
						JuiSlider slider = (JuiSlider) annotation;
						component = new Slider("eccolo", slider.min(), slider.max(), slider.value());
		            	context.add(component);
		            	
		            } else if ( annotation instanceof JuiText) {
						
						JuiText text = (JuiText) annotation;
						component = new Text(text.text(), text.input(), text.readonly());
		            	context.add(component);
		            }
				}
			} else {
			
	            Class<?> fieldType = field.getType();

				try {

		            if (fieldType == int.class) {
		            	
		            	component = new Text("", true, false);
		            	context.add(component );
		            	
		            	//context.add(new Slider("eccolo", column.min(), column.max(), 15));
		            }
		                
				} catch (IllegalArgumentException e) {
					
					log.warn("Somthng wrong adding object.Err[{}]", e.getLocalizedMessage());
				}
				
			}
			
			if ( component != null ) {
				Jui[] jui = field.getAnnotationsByType(Jui.class);
				if ( jui.length > 0 ) {
					component.setKey(jui[0].key()); 
				}
			}
			
		}
	}

}