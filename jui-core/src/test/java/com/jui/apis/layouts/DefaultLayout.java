package com.jui.apis.layouts;

import static com.jui.JuiApp.jui;
import static com.jui.JuiApp.linkedMapOf;

import java.util.List;

import lombok.extern.java.Log;

@Log
public class DefaultLayout {

	public static void main(String[] args)  {

		jui.markdown("""
        		# Main Content
        		Triggering server method from html *button*
        		""");

        jui.divider().color("blue");
        
        jui.button("Click Me!!", "primary", "alert('Button clicked!')", () -> {
        	log.info("Botton Clicked! Executing Server-side Code");
    	});
        
        jui.container("One").markdown("""
        		# Container One
        		""");
        
        jui.container("Two").markdown("""
        		# Container Two
        		""");
        
        jui.columns(linkedMapOf("left", 8, "right", 4));
        
        jui.columns("left").markdown("""
        		# Container Left
        		""");
        
        jui.columns("right").markdown("""
        		# Container Right
        		""");
        
        /*
        jui.dialog("", "");
        
        jui.expander("", "");
        
        jui.popover("", "");
        
        jui.empty("");
        */
        
        jui.tabs(List.of("tab1", "tab2", "tab3"));
        jui.tabs("tab1").markdown("""
        		# Container Tab1
        		""");
        
        jui.tabs("tab2").markdown("""
        		# Container Tab2
        		""");
        
        jui.start();

	}
}
