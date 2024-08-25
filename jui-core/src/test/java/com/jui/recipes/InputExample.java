package com.jui.recipes;

import static com.jui.JuiCore.jui;
import static com.jui.JuiCore.startJuiServer;
import static com.jui.utils.CSV.readAsTable;

import java.io.IOException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.jui.html.input.InputBuilder;


public class InputExample {
	
	@Test
	void InputSelect() throws IOException {
		
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

    	startJuiServer();
    	
    }
	
	@AfterAll
	public static void tearDown() {
	    while (true) { try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	    }
	}
	
	

}
