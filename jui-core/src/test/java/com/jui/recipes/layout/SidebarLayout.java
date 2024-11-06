package com.jui.recipes.layout;

import static com.jui.JuiApp.jui;

import com.jui.tests.utils.TestUtils;

import lombok.extern.java.Log;

@Log
public class SidebarLayout {

	public static void main(String[] args)  {

		TestUtils.initializedLogManager();
		
		jui.set_page_config()
			.layout("sidebar");
		
		jui.markdown("""
        		# Esempio SideBar
        		piccolo esempio *buttone*
        		""");

        jui.divider();
        
        jui.button("Cliccami", "primary", "alert('Button clicked!')", () -> {
        	log.info("Bottone cliccato! Esegui codice Server-side");
    	});
        
        jui.start();

	}
}
