package com.jui.recipes.layout;

import static com.jui.JuiApp.jui;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

import lombok.extern.java.Log;

@Log
public class SidebarLayout {

	public static void main(String[] args)  {

		//VM arguments -Djava.util.logging.config.file=src/test/resources/logging.properties
		try (InputStream inputStream = SidebarLayout.class.getResourceAsStream("/logging.properties")) {
		    LogManager.getLogManager().readConfiguration(inputStream);
		} catch (final IOException e) {
			log.severe("Could not load default logging.properties file.Err: " + e.getMessage());
		}
		
		jui.set_page_config()
			.layout("sidebar")
			.page("");
		
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
