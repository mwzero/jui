package com.jui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JuiSlider  {
	
	public String label() default "";
	public int min() default 0;
	public int max() default 10;
	public int value() default 10;
	 
}
