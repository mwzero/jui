package com.jui.recipes;

import static com.jui.JuiCore.jui;
import static com.jui.utils.CSV.readAsTable;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.jui.html.input.InputBuilder;


public class InputExample {
	
	public static void main(String... args) throws FileNotFoundException, IOException {
		
		jui.setTemplate("simple-bootstrap-1");
		jui.text.header("Input Example", "blue");
    	
    	var select = jui.input.select("city", readAsTable(true, "cities.csv", ","));
    	jui.input.input( InputBuilder.builder()
    			.label("city")
    			.placeholder("select a city")
    			.c_value(select)
    			.input(true)
    			.readonly(false)
    			.build());
    	
    	jui.input.submitbutton("Submit Data", null);

    	jui.startJuiServer();
    	
    }

}
