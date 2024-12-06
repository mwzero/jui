package com.jui.apis;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class TextElementsTest {

	public static void main(String[] args)  {
	
		log.info("Status Elements API");
		
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
        
        jui.text("this is :blue[pre-formatted] text :sunglasses: :angry:");
        
        jui.html("<p><span style='text-decoration: line-through double red;'>Oops</span>!</p>");
        
        jui.html("""
        		<p>using icons: 
        			<i class="bi bi-1-square-fill"></i>
        			<i class="bi bi-2-square"></i>
        			<i class="bi bi-3-square"></i>
        			<i class="bi bi-4-square"></i>
        			<i class="bi bi-5-square-fill"></i>
        		</p>""");
        
        
        jui.start();

	}
}
