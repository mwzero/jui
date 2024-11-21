package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class TextElements {

	public static void main(String[] args)  {
	
		log.info("Text Elements API");
		
        jui.markdown("""
        		# Text Elements APIs
        		""");

        jui.code("""
        		System.out.println("Hello World");
        		""", "Java", true);
        
        jui.start();

	}
}
