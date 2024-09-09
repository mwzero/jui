package com.jui.recipes;

import static com.jui.JuiApp.jui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;

import com.jui.html.input.FormButton.ButtonType;
import com.st.ST;

public class AllYouCanEat {
	
	public static void main(String... args) throws FileNotFoundException, IOException, URISyntaxException {
		
		jui.text.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
    	
		jui.input.button("Click me", "primary", "alert('Button clicked!')");
    	var i1 = jui.input.slider("slider1", 0, 100, 50);
    	
    	jui.divider();
    	jui.input.input("City", "", "");
    	
    	jui.divider();
    	var lines = jui.chart.bars(ST.read_csv("/my_data_1.csv"), 300, 300);
    	var bars= jui.chart.lines(ST.read_csv("/my_data_2.csv"), 300, 300);
    	//var map= page.chart.map(readCSV(true, "my_data_2.csv"));
    	
    	jui.divider();
    	var radio = jui.input.radio("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var checkbox = jui.input.checkbox("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var select = jui.input.select("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	var file = jui.input.file_uploader("Pick a file");
    	var color  = jui.input.color_picker("Pick a color");
    	var date  = jui.input.date_input("Pick a date/");
    	
    	jui.input.formButton("Click me", ButtonType.Primary, ";");
    	
    	jui.start();
    	
    }
}
