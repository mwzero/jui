package com.jui;

import static com.jui.JuiApp.jui;

public class SimpleRecipe {

	public static void main(String[] args)  {

        jui.button("Cliccami", "primary", "alert('Button clicked!')", () -> {
        		System.out.println("Bottone cliccato! Esegui codice Server-side");
        	});
        
		jui.start();

	}
}
