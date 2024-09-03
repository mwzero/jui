package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.jui.html.input.InputBuilder;
import com.st.ST;


public class InputExample {
	
	public static void main(String... args) throws FileNotFoundException, IOException {
		
		jui.text.header("Input Example", "blue");
    	
    	var select = jui.input.select("city", ST.builder().option("classLoading", "true").build().readAsTable("cities.csv", ","));
    	jui.input.input( InputBuilder.builder()
    			.label("city")
    			.placeholder("select a city")
    			.c_value(select)
    			.input(true)
    			.readonly(false)
    			.build());
    	
    	jui.input.submitbutton("Submit Data", null);

    	jui.start();
    	
    }

}
