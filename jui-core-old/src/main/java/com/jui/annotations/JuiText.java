package com.jui.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface JuiText  {
	
	public String text() default "";
	public boolean input() default true;
	public boolean readonly() default false;
	 
}
