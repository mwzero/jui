package com.jui.recipes.layout;

import static com.jui.JuiApp.jui;

import lombok.extern.java.Log;

@Log
public class DefaultLayout {

	public static void main(String[] args)  {

		jui.markdown("""
        		# Esempio
        		piccolo esempio *buttone*
        		""");

        jui.divider();
        
        jui.button("Cliccami", "primary", "alert('Button clicked!')", () -> {
        	log.info("Bottone cliccato! Esegui codice Server-side");
    	});
        
        jui.start();

	}
}
