package com.jui.recipes;

import static com.jui.JuiApp.jui;
import static com.st.ST.st;

import java.util.Arrays;

import com.jui.html.tags.FormButton.ButtonType;

public class AllYouCanEat {
	
	public static void main(String... args) throws Exception {
		
		jui.markdown("""
    			# JUI
    			*JUI* build web applications from Java.
    			No need to write a backend, define routes, handle HTTP requests, connect a frontend, write HTML, CSS, JavaScript, ...
    			""");
    	
		//jui.input.button("Click me", "primary", "alert('Button clicked!')");
    	jui.slider("slider1", 0, 100, 50);
    	
    	jui.divider();
    	jui.input("City", "", "");
    	
    	jui.divider();
    	jui.bars()
    		.df(st.read_csv("/my_data_1.csv"))
    		.max_width(300)
    		.max_height(300);
    	
    	jui.lines()
    		.df(st.read_csv("/my_data_2.csv"))
    		.width(300)
    		.height(300);
    	
    	//var map= page.chart.map(readCSV(true, "my_data_2.csv"));
    	
    	jui.divider();
    	jui.radio("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.checkbox("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.select("Pick a value", Arrays.asList(new String[]{"foo", "bar"}));
    	jui.file_uploader("Pick a file");
    	jui.color_picker("Pick a color");
    	jui.date_input("Pick a date/");
    	
    	jui.formButton("Click me", ButtonType.Primary, ";");
    	
    	jui.start();
    	
    }
}
