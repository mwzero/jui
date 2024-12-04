package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class TextElements {

	public static void main(String[] args)  {
	
		log.info("Text Elements API");
		
		jui.title("This is a title text");
		
		jui.header("this is a header text");
        
        jui.subheader("this is a sub-header text");
        
        jui.divider();
		
        jui.markdown("""
        		# this is markdown Text
        		""");
        
        jui.divider();
        
        jui.caption("this is a caption text");

        jui.code("""
        		System.out.println("Hello World");
        		""", "Java", true);
        
        
        
        jui.text("this is :blue[pre-formatted] text :sunglasses:");
        
        jui.html("<p><span style='text-decoration: line-through double red;'>Oops</span>!</p>");
        
        
        jui.start();

	}
}
