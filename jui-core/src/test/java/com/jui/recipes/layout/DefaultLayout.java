package com.jui.recipes.layout;

import static com.jui.JuiApp.jui;

import java.util.LinkedHashMap;
import java.util.Map;

import lombok.extern.java.Log;

@Log
public class DefaultLayout {

	public static void main(String[] args)  {

		jui.markdown("""
        		# Main Content
        		Triggering server method from html *button*
        		""");

        jui.divider();
        
        jui.button("Click Me!!", "primary", "alert('Button clicked!')", () -> {
        	log.info("Botton Clicked! Executing Server-side Code");
    	});
        
        jui.addContainer("One").markdown("""
        		# Container One
        		""");
        
        jui.addContainer("Two").markdown("""
        		# Container Two
        		""");
        
        jui.columns(jui.linkedMapOf("left", 8, "right", 4));
        
        jui.columns("left").markdown("""
        		# Container Left
        		""");
        
        jui.columns("right").markdown("""
        		# Container Right
        		""");
        
        jui.start();

	}
}
