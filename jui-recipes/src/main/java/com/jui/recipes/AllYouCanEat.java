package com.jui.recipes;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.util.Arrays;

import com.jui.html.FormButton.ButtonType;

public class AllYouCanEat {
	
	public static void main(String... args) throws Exception {
		
		jui.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
    	
		//jui.input.button("Click me", "primary", "alert('Button clicked!')");
    	jui.input.slider("slider1", 0, 100, 50);
    	
    	jui.divider();
    	jui.input.input("City", "", "");
    	
    	jui.divider();
    	jui.chart.bars(st.read_csv("/my_data_1.csv"), 300, 300);
    	jui.chart.lines(st.read_csv("/my_data_2.csv"), 300, 300);
    	//var map= page.chart.map(readCSV(true, "my_data_2.csv"));
    	
    	jui.divider();
    	jui.input.radio("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.input.checkbox("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.input.select("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.input.file_uploader("Pick a file");
    	jui.input.color_picker("Pick a color");
    	jui.input.date_input("Pick a date/");
    	
    	jui.input.formButton("Click me", ButtonType.Primary, ";");
    	
    	jui.start();
    	
    }
}
