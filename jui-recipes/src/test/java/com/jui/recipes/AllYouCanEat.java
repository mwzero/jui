package com.jui.recipes;

import static com.jui.JuiCore.jui;
import static com.jui.utils.CSV.readAsList;

import java.io.IOException;
import java.util.Arrays;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import com.jui.html.input.FormButton;

public class AllYouCanEat {
	
	@Test
    void simple() throws IOException {
    	
		jui.setTemplate("simple-bootstrap-1");
    	
		jui.text.markdown("""
    			# JUI
    			*JUI*, puoi trasformare rapidamente i tuoi script Java in applicazioni web complete senza dover scrivere codice HTML, CSS o JavaScript.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
    	
		jui.input.button("Click me", "primary", "alert('Button clicked!')");
    	var i1 = jui.input.slider("slider1", 0, 100, 50);
    	
    	jui.divider();
    	jui.input.input("Eccolo", "", "");
    	
    	jui.divider();
    	var lines = jui.chart.bars(readAsList(true, "my_data_1.csv"), 300, 300);
    	var bars= jui.chart.lines(readAsList(true, "my_data_2.csv"), 300, 300);
    	//var map= page.chart.map(readCSV(true, "my_data_2.csv"));
    	
    	jui.divider();
    	var radio = jui.input.radio("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var checkbox = jui.input.checkbox("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var select = jui.input.select("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var file = jui.input.file_uploader("Pick a file");
    	var color  = jui.input.color_picker("Pick a color");
    	var date  = jui.input.date_input("Pick a date/");
    	
    	jui.input.formButton("Click me", FormButton.ButtonType.Primary, ";");
    	
    	jui.startJuiServer();
    	
    	
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
